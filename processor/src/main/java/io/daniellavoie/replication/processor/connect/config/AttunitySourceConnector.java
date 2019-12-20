package io.daniellavoie.replication.processor.connect.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;

@Component
public class AttunitySourceConnector implements SourceConnectorConfiguration {

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
}
