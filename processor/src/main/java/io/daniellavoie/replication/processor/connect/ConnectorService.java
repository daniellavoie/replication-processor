package io.daniellavoie.replication.processor.connect;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.daniellavoie.replication.processor.connect.model.ConnectorConfigValidationResult;
import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition;
import io.daniellavoie.replication.processor.source.SourceConnectorConfiguration;

public interface ConnectorService {
	List<ConnectorInstance> buildSinkConnectorInstances(ReplicationDefinition replicationDefinition);

	Optional<ConnectorInstance> buildSourceConnectorInstance(ReplicationDefinition replicationDefinition);

	SourceConnectorConfiguration getSourceConnectorConfiguration(SourceDefinition.Type type);
	
	ConnectorConfigValidationResult validateConnectorConfig(Map<String, String> configs,
			ConnectorConfiguration connectorConfiguration);
}
