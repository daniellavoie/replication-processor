package io.daniellavoie.replication.processor.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import io.daniellavoie.replication.processor.connect.model.ConnectorStatusResponse;
import reactor.core.publisher.Mono;

@Service
public class ConnectServiceImpl implements ConnectService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectServiceImpl.class);

	private final WebClient connectWebClient;

	public ConnectServiceImpl(WebClient connectWebClient) {
		this.connectWebClient = connectWebClient;
	}

	private Mono<Void> cleanConnector(String name) {
		return connectWebClient.delete().uri("/connectors/{name}", name)

				.exchange()

				.doOnSubscribe(subscriber -> LOGGER.info("Deleting connector instance {}.", name))

				.then();
	}

	public Mono<ConnectorInstance> createConnector(ConnectorInstance connectorInstance) {
		return cleanConnector(connectorInstance.getName())

				.doOnSubscribe(subscriber -> LOGGER.info("Creating connector {}.", connectorInstance))

				.then(connectWebClient.post().uri("/connectors").bodyValue(connectorInstance)

						.exchange()

						.flatMap(response -> handleResponse(response, ConnectorInstance.class)))

				.log();
	}

	private <T> Mono<T> handleResponse(ClientResponse clientResponse, Class<T> responseType) {
		if (clientResponse.statusCode().is2xxSuccessful()) {
			return clientResponse.bodyToMono(responseType);
		} else {
			return clientResponse.createException().flatMap(exception -> Mono.<T>error(exception));
		}
	}

	@Override
	public Mono<Void> refreshConnector(ConnectorInstance connectorInstance) {
		return getConnectorStatus(connectorInstance.getName())

				.onErrorResume(this::is404Error, throwable -> Mono.<ConnectorStatusResponse>empty())

				.filter(connectorStatusResponse -> connectorStatusResponse.getConnector().getState().equals("RUNNING"))

				.flatMap(connectorStatusResponse -> getConnectorInstance(connectorInstance.getName()))

				.filter(existingConnectorInstance -> configurationMatches(connectorInstance, existingConnectorInstance))

				// Mono will be empty if connector is not in a good state.
				.switchIfEmpty(cleanConnector(connectorInstance.getName())

						.then(createConnector(connectorInstance)))

				.then()

				.doOnSubscribe(subscriber -> LOGGER.info("Refreshing connector {}.", connectorInstance.getName()));

	}

	public boolean configurationMatches(ConnectorInstance connectorInstance,
			ConnectorInstance existingConnectorInstance) {
		Assert.notNull(existingConnectorInstance,
				"Existing connector instance is undefined for " + connectorInstance.getName() + ".");
		Assert.notNull(existingConnectorInstance.getConfig(),
				"Configuration is undefined for existing connector instance " + connectorInstance.getName() + ".");

		boolean mismatch = existingConnectorInstance.getConfig().entrySet().stream()
				.filter(entry -> existingConnectorInstance.getConfig().containsKey(entry.getKey()))
				.filter(entry -> !existingConnectorInstance.getConfig().get(entry.getKey()).equals(entry.getValue()))
				.count() != 0
				|| connectorInstance.getConfig().entrySet().stream()
						.filter(entry -> !existingConnectorInstance.getConfig().containsKey(entry.getKey()))
						.count() != 0
				|| existingConnectorInstance.getConfig().entrySet().stream()
						.filter(existingEntry -> !connectorInstance.getConfig().containsKey(existingEntry.getKey()))
						.count() != 0;

		if (!mismatch) {
			LOGGER.info("New configuration matches for connector instance {}.", connectorInstance.getName());
		} else {
			LOGGER.info("New configuration mismatch for connector instance {}.", connectorInstance.getName());
		}

		return !mismatch;
	}

	private Mono<ConnectorInstance> getConnectorInstance(String name) {
		return connectWebClient.get().uri("/connectors/{name}", name)

				.exchange()

				.flatMap(response -> handleResponse(response, ConnectorInstance.class));
	}

	private Mono<ConnectorStatusResponse> getConnectorStatus(String name) {
		return connectWebClient.get().uri("/connectors/{name}/status", name)

				.exchange()

				.flatMap(response -> handleResponse(response, ConnectorStatusResponse.class));
	}

	private boolean is404Error(Throwable throwable) {
		return throwable instanceof WebClientResponseException.NotFound;
	}
}
