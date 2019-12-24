package io.daniellavoie.replication.processor.source.debezium;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.daniellavoie.replication.processor.config.ConnectConfiguration;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.source.AbstractConnectorConfiguration;
import reactor.core.publisher.Flux;

public abstract class DebeziumSourceConfiguration extends AbstractConnectorConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumSourceConfiguration.class);

	private final ConnectConfiguration connectConfiguration;

	public DebeziumSourceConfiguration(ConnectConfiguration connectConfiguration) {
		super(DateFormat.NANOSECONDS);
		
		this.connectConfiguration = connectConfiguration;
	}

	@Override
	public Flux<GenericRecord> extractRecord(byte[] sourceEvent, Schema schema) {
		return Flux.just(readJson(sourceEvent, Event.class))

				.filter(this::filterMisingPayload)

				.map(event -> buildRecordFromMap(event.getPayload().getAfter(), schema));
	}

	private boolean filterMisingPayload(Event event) {
		if (event.getPayload() == null) {
			LOGGER.warn("Payload is missing from Debezium event.");

			return false;

		} else if (event.getPayload().getAfter() == null) {
			LOGGER.warn("After struct is missing from Debezium event payload.");
			return false;
		}

		return true;
	}

	@Override
	public Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition) {
		Map<String, String> defaultValues = new HashMap<>();

		defaultValues.put("database.server.name", replicationDefinition.getName());
		defaultValues.put("database.history.kafka.bootstrap.servers", connectConfiguration.getKafkaBootstrapServers());
		defaultValues.put("database.history.kafka.topic", "debezium-history-" + replicationDefinition.getName());

		return defaultValues;
	}

	@Override
	public Optional<String> getKeyConverter() {
		return Optional.of("org.apache.kafka.connect.storage.StringConverter");
	}

	@Override
	public String getValueConverter() {
		return "org.apache.kafka.connect.json.JsonConverter";
	}

	@Override
	public boolean requiresInstance() {
		return true;
	}
}
