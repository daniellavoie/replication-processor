package io.daniellavoie.replication.processor.connect;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.daniellavoie.replication.processor.connect.config.ConnectorConfiguration;
import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;

public interface ConnectorService {
	List<ConnectorInstance> buildSinkConnectorInstances(ReplicationDefinition replicationDefinition);

	Optional<ConnectorInstance> buildSourceConnectorInstance(ReplicationDefinition replicationDefinition);

	ConnectorConfigValidationResult validateConnectorConfig(Map<String, String> configs,
			ConnectorConfiguration connectorConfiguration);
}
