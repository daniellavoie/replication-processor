package io.daniellavoie.replication.processor.config;

import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.replication.processor.core.ReplicationDefinitionProcessor;
import io.daniellavoie.replication.processor.core.ReplicationDefinitionUpdateListener;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.store.ReplicationDefinitionStoreConfiguration;
import io.daniellavoie.replication.processor.topic.ReplicationDefinitionConfiguration;
import io.daniellavoie.replication.processor.topic.TopicUtil;
import reactor.core.publisher.DirectProcessor;

@Configuration
public class KafkaStreamsConfig {
	@Bean
	public Topology topology(ReplicationDefinitionConfiguration replicationDefinitionConfiguration,
			ReplicationDefinitionStoreConfiguration replicationDefinitionStoreConfiguration, AdminClient adminClient,
			ObjectMapper objectMapper, StreamsBuilder streamsBuilder,
			List<ReplicationDefinitionUpdateListener> replicationDefinitionUpdateListeners) {
		TopicUtil.createTopicIfMissing(replicationDefinitionConfiguration, adminClient);

		streamsBuilder.addStateStore(Stores.keyValueStoreBuilder(
				Stores.persistentKeyValueStore(replicationDefinitionStoreConfiguration.getName()), Serdes.String(),
				new JsonSerde<ReplicationDefinition>(ReplicationDefinition.class, objectMapper)));

		KStream<String, ReplicationDefinition> stream = streamsBuilder.stream(
				replicationDefinitionConfiguration.getName(),
				Consumed.with(Serdes.String(), new JsonSerde<ReplicationDefinition>(ReplicationDefinition.class)));

		DirectProcessor<ReplicationDefinition> replicationDefinitionProcessor = DirectProcessor.create();
		for (ReplicationDefinitionUpdateListener listener : replicationDefinitionUpdateListeners) {
			replicationDefinitionProcessor.onBackpressureBuffer()

					.doOnNext(listener::onUpdate)

					.doOnError(listener::onError)

					.subscribe();
		}

		stream.process(() -> new ReplicationDefinitionProcessor(replicationDefinitionStoreConfiguration.getName(),
				replicationDefinitionProcessor), replicationDefinitionStoreConfiguration.getName());

		return streamsBuilder.build();
	}
}
