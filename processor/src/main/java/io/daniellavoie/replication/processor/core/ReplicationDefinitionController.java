package io.daniellavoie.replication.processor.core;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/replication-definition")
public class ReplicationDefinitionController {

	private final ReplicationDefinitionService replicationService;

	public ReplicationDefinitionController(ReplicationDefinitionService replicationService) {
		this.replicationService = replicationService;

	}

	@DeleteMapping("/{name}")
	public Mono<Void> delete(@PathVariable String name) {
		return replicationService.delete(name);
	}

	@GetMapping
	public Flux<ReplicationDefinition> findAll() {
		return replicationService.findAll();
	}

	@GetMapping("/{name}")
	public Mono<ReplicationDefinition> findOne(@PathVariable String name) {
		return replicationService.findOne(name);
	}

	@PostMapping
	public Mono<ReplicationDefinition> save(@RequestBody ReplicationDefinition replicationDefinition) {
		return replicationService.save(replicationDefinition);
	}

}
