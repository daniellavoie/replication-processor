package io.daniellavoie.replication.processor.core;

import java.util.Optional;

import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.store.ReplicationDefinitionStoreConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ReplicationDefinitionKafkaController.ENDPOINT)
public class ReplicationDefinitionKafkaController {
	public static final String ENDPOINT = "/kafka-streams/replication-definition";

	private final ReplicationDefinitionStoreConfiguration replicationDefinitionStoreConfiguration;
	private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

	public ReplicationDefinitionKafkaController(ReplicationDefinitionService replicationService,
			ReplicationDefinitionStoreConfiguration replicationDefinitionStoreConfiguration,
			StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
		this.replicationDefinitionStoreConfiguration = replicationDefinitionStoreConfiguration;
		this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
	}

	@GetMapping
	public Flux<ReplicationDefinition> findAll() {
		return Flux.fromIterable(() -> getStore().all()).map(keyValue -> keyValue.value);
	}

	@GetMapping("/{name}")
	public Mono<ReplicationDefinition> findOne(@PathVariable String name) {
		return Mono.justOrEmpty(Optional.ofNullable(getStore().get(name)));
	}

	private ReadOnlyKeyValueStore<String, ReplicationDefinition> getStore() {
		return streamsBuilderFactoryBean.getKafkaStreams().store(replicationDefinitionStoreConfiguration.getName(),
				QueryableStoreTypes.keyValueStore());
	}

}
