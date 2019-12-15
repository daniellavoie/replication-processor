package io.daniellavoie.replication.processor.core;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;

public interface ReplicationDefinitionUpdateListener {
	void onUpdate(ReplicationDefinition replicationDefinition);
	
	void onError(Throwable cause);
}
