package io.daniellavoie.replication.processor.sink;

import java.util.Map;

import io.daniellavoie.replication.processor.connect.ConnectorConfiguration;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SinkDefinition;
import io.daniellavoie.replication.processor.model.SinkDefinition.Type;

public interface SinkConnectorConfiguration extends ConnectorConfiguration {

	Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition, SinkDefinition sinkDefinition);
	
	Type getSinkType();

}
