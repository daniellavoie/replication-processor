package io.daniellavoie.replication.processor.core;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;

public interface ReplicationDefinitionListener {
	void onDelete(ReplicationDefinition replicationDefinition);
	
	void onUpdate(ReplicationDefinition replicationDefinition);
	
	void onError(Throwable cause);
}
