package io.daniellavoie.replication.processor.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;

import io.daniellavoie.replication.processor.connect.ConnectService;
import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SinkDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition;
import io.daniellavoie.replication.processor.store.ReplicationDefinitionStoreConfiguration;
import io.daniellavoie.replication.processor.topic.ReplicationDefinitionConfiguration;
import io.daniellavoie.replication.processor.topology.TopologyServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReplicationDefinitionServiceImpl
		implements ReplicationDefinitionService, ReplicationDefinitionUpdateListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationDefinitionServiceImpl.class);

	private final KafkaStreamsMetaService kafkaStreamsMetaService;
	private final ConnectService connectService;
	private final TopologyServiceImpl topologyService;
	private final String replicationDefinitionStoreName;
	private final String topicName;
	private final KafkaTemplate<String, ReplicationDefinition> kafkaTemplate;

	private final WebClient webClient = WebClient.create();

	public ReplicationDefinitionServiceImpl(KafkaStreamsMetaService kafkaStreamsMetaService,
			ConnectService connectService, TopologyServiceImpl topologyService,
			ReplicationDefinitionConfiguration replicationDefinitionConfiguration,
			ReplicationDefinitionStoreConfiguration replicationDefinitionStoreConfiguration,
			KafkaProperties kafkaProperties) {
		this.kafkaStreamsMetaService = kafkaStreamsMetaService;
		this.connectService = connectService;
		this.topologyService = topologyService;
		this.topicName = replicationDefinitionConfiguration.getName();
		this.replicationDefinitionStoreName = replicationDefinitionStoreConfiguration.getName();

		this.kafkaTemplate = new KafkaTemplate<String, ReplicationDefinition>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));

	}

	private Mono<Void> startReplication(ReplicationDefinition replicationDefinition) {

		// Refresh the sink connectors if required.
		return Flux.fromIterable(replicationDefinition.getSinks())
				.map(sinkDefinition -> buildSinkConnectorInstance(replicationDefinition.getSinkTopic().getName(),
						replicationDefinition.getName(), sinkDefinition))

				.flatMap(connectService::refreshConnector)

				// Refresh the source connector if required.
				.then(Mono.just(requiresSourceConnector(replicationDefinition)).filter(Boolean::booleanValue)

						.map(requiresSourceConnector -> buildSourceConnectorInstance(
								replicationDefinition.getSinkTopic().getName(), replicationDefinition.getName(),
								replicationDefinition.getSource()))

						.flatMap(connectService::refreshConnector))

				.then(topologyService.runTopopoly(replicationDefinition))

				.doOnSubscribe(subscriber -> LOGGER.info("Starting replication {}", replicationDefinition.getName()));
	}

	private ConnectorInstance buildSinkConnectorInstance(String sinkTopic, String replicationName,
			SinkDefinition sinkDefinition) {
		return new ConnectorInstance(replicationName + "-" + sinkDefinition.getName() + "-sink",
				buildSinkConnectorConfig(sinkTopic, sinkDefinition));
	}

	private ConnectorInstance buildSourceConnectorInstance(String sourceTopic, String replicationName,
			SourceDefinition sourceDefinition) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	private Map<String, String> buildSinkConnectorConfig(String sinkTopic, SinkDefinition sinkDefinition) {
		Map<String, String> config = new HashMap<>();

		if (sinkDefinition.getType().equals(SinkDefinition.Type.SQLSERVER)) {
			config.put("connector.class", "io.confluent.connect.jdbc.JdbcSinkConnector");
			config.put("tasks.max", String.valueOf(sinkDefinition.getTasksMax()));
			config.put("topics", sinkTopic);
			config.put("connection.url", sinkDefinition.getSqlServerConfiguration().getConnectionUrl());
			config.put("connection.user", sinkDefinition.getSqlServerConfiguration().getUser());
			config.put("connection.password", sinkDefinition.getSqlServerConfiguration().getPassword());
			config.put("auto.create", String.valueOf(true));
			config.put("insert.mode", "upsert");
			config.put("pk.mode", "record_value");
			config.put("pk.fields", sinkDefinition.getSqlServerConfiguration().getPkFields());
		} else {
			throw new RuntimeException(sinkDefinition.getType() + " is not a supported sink type.");
		}

		return config;
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
	public void onUpdate(ReplicationDefinition replicationDefinition) {
		startReplication(replicationDefinition).

				doOnError(this::onError)

				.subscribe();
	}

	@Override
	public void onError(Throwable cause) {
		LOGGER.error("An error occured while processing replication definition updates.", cause);
	}

	private boolean requiresSourceConnector(ReplicationDefinition replicationDefinition) {
		return replicationDefinition.getSource().getType().equals(SourceDefinition.Type.SQLSERVER);
	}

	@Override
	public Mono<ReplicationDefinition> save(ReplicationDefinition replicationDefinition) {
		return Mono

				.fromFuture(kafkaTemplate.send(topicName, replicationDefinition.getName(), replicationDefinition)
						.completable().toCompletableFuture())

				.map(sendResult -> sendResult.getProducerRecord().value());
	}
}
