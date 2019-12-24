package io.daniellavoie.replication.processor.source.attunity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.daniellavoie.replication.processor.connect.model.ConnectorConfigField;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;
import io.daniellavoie.replication.processor.source.AbstractConnectorConfiguration;
import reactor.core.publisher.Flux;

@Component
public class AttunitySourceConnector extends AbstractConnectorConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(AttunitySourceConnector.class);

	@Override
	public String getConnectorClass() {
		throw new UnsupportedOperationException("Attunity does not require a connector.");
	}

	@Override
	public Optional<String> getKeyConverter() {
		throw new UnsupportedOperationException("Attunity does not require a connector.");
	}

	@Override
	public Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition) {
		throw new UnsupportedOperationException("Attunity does not require a connector.");
	}

	@Override
	public List<ConnectorConfigField> getFields() {
		throw new UnsupportedOperationException("Attunity does not require a connector.");
	}

	@Override
	public Type getSourceType() {
		return Type.ATTUNITY;
	}

	@Override
	public String getValueConverter() {
		return "org.apache.kafka.connect.json.JsonConverter";
	}

	@Override
	public boolean requiresInstance() {
		return false;
	}

	@Override
	public Flux<GenericRecord> extractRecord(byte[] sourceEvent, Schema schema) {
		Event event = readJson(sourceEvent, Event.class);

		if (event.getMessage() == null) {
			LOGGER.warn("Message is missing from Attunity event.");
			return Flux.empty();
		} else if (event.getMessage().getData() == null) {
			LOGGER.warn("Data struct is missing from Attunity event message.");
			return Flux.empty();
		}

		return Flux.just(buildRecordFromMap(readJson(sourceEvent, Event.class).getMessage().getData(), schema));
	}
}
