package io.daniellavoie.replication.processor.core;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;

import io.daniellavoie.replication.processor.connect.ConnectService;
import io.daniellavoie.replication.processor.connect.ConnectorService;
import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.store.ReplicationDefinitionStoreConfiguration;
import io.daniellavoie.replication.processor.topic.ReplicationDefinitionConfiguration;
import io.daniellavoie.replication.processor.topology.TopologyServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReplicationDefinitionServiceImpl implements ReplicationDefinitionService, ReplicationDefinitionListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationDefinitionServiceImpl.class);

	private final ConnectorService connectorService;
	private final KafkaStreamsMetaService kafkaStreamsMetaService;
	private final ConnectService connectService;
	private final TopologyServiceImpl topologyService;
	private final String replicationDefinitionStoreName;
	private final String topicName;
	private final KafkaTemplate<String, ReplicationDefinition> kafkaTemplate;

	private final WebClient webClient = WebClient.create();

	public ReplicationDefinitionServiceImpl(ConnectorService connectorService,
			KafkaStreamsMetaService kafkaStreamsMetaService, ConnectService connectService,
			TopologyServiceImpl topologyService, ReplicationDefinitionConfiguration replicationDefinitionConfiguration,
			ReplicationDefinitionStoreConfiguration replicationDefinitionStoreConfiguration,
			KafkaProperties kafkaProperties) {
		this.connectorService = connectorService;
		this.kafkaStreamsMetaService = kafkaStreamsMetaService;
		this.connectService = connectService;
		this.topologyService = topologyService;
		this.topicName = replicationDefinitionConfiguration.getName();
		this.replicationDefinitionStoreName = replicationDefinitionStoreConfiguration.getName();

		this.kafkaTemplate = new KafkaTemplate<String, ReplicationDefinition>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
	}

	@Override
	public Mono<Void> delete(String name) {
		return Mono

				.fromFuture(kafkaTemplate.send(topicName, name, null).completable().toCompletableFuture()).then();
	}

	@Override
	public Flux<ReplicationDefinition> findAll() {
		return Flux.fromIterable(kafkaStreamsMetaService.findEndpointsForStore(replicationDefinitionStoreName))

				.flatMap(remoteEndpoint -> webClient.get().uri(remoteEndpoint)

						.exchange()

						.flatMapMany(response -> response.bodyToFlux(ReplicationDefinition.class)));
	}

	@Override
	public Mono<ReplicationDefinition> findOne(@PathVariable String name) {
		return webClient.get().uri(kafkaStreamsMetaService.findEndpointForKey(replicationDefinitionStoreName, name))

				.exchange()

				.flatMap(response -> response.bodyToMono(ReplicationDefinition.class));
	}

	@Override
	public void onDelete(ReplicationDefinition replicationDefinition) {
		stopReplication(replicationDefinition)

				.doOnError(this::onError)

				.subscribe();
	}

	@Override
	public void onError(Throwable cause) {
		LOGGER.error("An error occured while processing replication definition updates.", cause);
	}

	@Override
	public void onUpdate(ReplicationDefinition replicationDefinition) {
		startReplication(replicationDefinition)

				.doOnError(this::onError)

				.subscribe();
	}

	@Override
	public Mono<ReplicationDefinition> save(ReplicationDefinition replicationDefinition) {
		return Mono

				.fromFuture(kafkaTemplate.send(topicName, replicationDefinition.getName(), replicationDefinition)
						.completable().toCompletableFuture())

				.map(sendResult -> sendResult.getProducerRecord().value());
	}

	private Mono<Void> startReplication(ReplicationDefinition replicationDefinition) {

		// Refresh the sink connectors if required.
		return Flux.fromIterable(connectorService.buildSinkConnectorInstances(replicationDefinition))

				.flatMap(connectService::refreshConnector)

				// Refresh the source connector if required.
				.then(connectorService.buildSourceConnectorInstance(replicationDefinition).map(Mono::just)
						.orElseGet(Mono::empty)

						.flatMap(connectService::refreshConnector))

				.then(topologyService.runTopopoly(replicationDefinition))

				.doOnSubscribe(subscriber -> LOGGER.info("Starting replication {}", replicationDefinition.getName()));
	}

	private Mono<Void> stopReplication(ReplicationDefinition replicationDefinition) {
		return Mono.just(connectorService.buildSourceConnectorInstance(replicationDefinition))
				.filter(Optional::isPresent)

				.map(Optional::get)

				.map(ConnectorInstance::getName)

				.flatMap(connectService::deleteConnector)

				.thenMany(Flux.fromIterable(connectorService.buildSinkConnectorInstances(replicationDefinition)))

				.map(ConnectorInstance::getName)

				.flatMap(connectService::deleteConnector)

				.then(topologyService.stopTopology(replicationDefinition));
	}
}
