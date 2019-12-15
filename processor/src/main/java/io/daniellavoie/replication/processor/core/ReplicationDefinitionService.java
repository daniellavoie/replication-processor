package io.daniellavoie.replication.processor.core;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReplicationDefinitionService {
	Flux<ReplicationDefinition> findAll();
	
	Mono<ReplicationDefinition> findOne(String name);
	
	Mono<ReplicationDefinition> save(ReplicationDefinition replicationDefinition);
}
