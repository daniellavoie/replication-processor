package io.daniellavoie.replication.processor.connect.config;

import java.util.Map;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;

public interface SourceConnectorConfiguration extends ConnectorConfiguration {
	Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition);

	Type getSourceType();
	
	String getValueConverter();

	boolean requiresInstance();
}
