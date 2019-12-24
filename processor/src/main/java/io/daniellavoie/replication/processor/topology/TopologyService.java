package io.daniellavoie.replication.processor.topology;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import reactor.core.publisher.Mono;

public interface TopologyService {
	Mono<Void> runTopopoly(ReplicationDefinition replicationDefinition);
	
	Mono<Void> stopTopology(ReplicationDefinition replicationDefinition);
}
