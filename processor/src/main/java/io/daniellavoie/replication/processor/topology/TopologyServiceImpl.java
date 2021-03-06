package io.daniellavoie.replication.processor.topology;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.daniellavoie.replication.processor.connect.ConnectorService;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.source.SourceConnectorConfiguration;
import io.daniellavoie.replication.processor.topic.TopicUtil;
import reactor.core.publisher.Mono;

@Service
public class TopologyServiceImpl implements TopologyService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TopologyServiceImpl.class);

	private final String applicationName;
	private final AdminClient adminClient;
	private final ConnectorService connectorService;
	private final KafkaProperties kafkaProperties;
	private final Properties baseProperties;

	private Map<String, KafkaStreams> streams = new HashMap<>();

	public TopologyServiceImpl(@Value("${spring.application.name}") String applicationName, AdminClient adminClient,
			ConnectorService connectorService, KafkaProperties kafkaProperties) {
		this.applicationName = applicationName;
		this.adminClient = adminClient;
		this.connectorService = connectorService;
		this.kafkaProperties = kafkaProperties;

		this.baseProperties = new Properties();
		this.baseProperties.putAll(kafkaProperties.buildStreamsProperties());
	}

	private Topology buildTopology(SourceConnectorConfiguration sourceConnectorConfiguration,
			ReplicationDefinition replicationDefinition) {
		StreamsBuilder streamsBuilder = new StreamsBuilder();

		TopicUtil.createTopicIfMissing(replicationDefinition.getSinkTopic(), adminClient);
		TopicUtil.createTopicIfMissing(replicationDefinition.getSourceTopic(), adminClient);

		Schema avroSinkSchema = new Parser().setValidate(true).parse(replicationDefinition.getSinkSchema());

		GenericAvroSerde genericAvroSerde = new GenericAvroSerde();
		genericAvroSerde.configure(kafkaProperties.buildStreamsProperties(), false);

		KStream<byte[], byte[]> eventStream = streamsBuilder.stream(replicationDefinition.getSourceTopic().getName(),
				Consumed.with(Serdes.ByteArray(), Serdes.ByteArray()));

		eventStream

				.flatMapValues(event -> sourceConnectorConfiguration.extractRecord(event, avroSinkSchema).collectList()
						.block())

				.to(replicationDefinition.getSinkTopic().getName(), Produced.valueSerde(genericAvroSerde));

		return streamsBuilder.build();
	}

	@Override
	public Mono<Void> runTopopoly(ReplicationDefinition replicationDefinition) {
		return Mono.create(sink -> {
			synchronized (streams) {
				KafkaStreams existingStreams = streams.get(replicationDefinition.getName());
				if (existingStreams != null) {
					LOGGER.info("Stopping existing Kafka Streams instance for {}.", replicationDefinition.getName());

					existingStreams.close();
				}

				Topology topology = buildTopology(
						connectorService.getSourceConnectorConfiguration(replicationDefinition.getSource().getType()),
						replicationDefinition);

				LOGGER.info("Starting new topology {}.", topology.describe());

				Properties streamsProperties = new Properties();
				streamsProperties.putAll(baseProperties);
				streamsProperties.put("application.id", applicationName + "-" + replicationDefinition.getName());

				KafkaStreams replicationStream = new KafkaStreams(topology, streamsProperties);

				streams.put(replicationDefinition.getName(), replicationStream);

				replicationStream.start();

			}

			sink.success();
		});
	}

	@Override
	public Mono<Void> stopTopology(ReplicationDefinition replicationDefinition) {
		return Mono.create(sink -> {
			synchronized (streams) {
				KafkaStreams existingStreams = streams.get(replicationDefinition.getName());
				if (existingStreams != null) {
					LOGGER.info("Stopping Kafka Streams instance for {}.", replicationDefinition.getName());

					existingStreams.close();

					streams.remove(replicationDefinition.getName());
				}
			}

			sink.success();
		});
	}
}
