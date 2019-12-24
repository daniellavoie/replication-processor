package io.daniellavoie.replication.processor.core;

import java.util.Optional;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

public class ReplicationDefinitionProcessor implements Processor<String, ReplicationDefinition> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationDefinitionProcessor.class);

	private final String storeName;

	private KeyValueStore<String, ReplicationDefinition> rateStore;
	private final DirectProcessor<ReplicationDefinition> updateProcessor;
	private final DirectProcessor<ReplicationDefinition> deleteProcessor;

	public ReplicationDefinitionProcessor(String storeName, DirectProcessor<ReplicationDefinition> updateProcessor,
			DirectProcessor<ReplicationDefinition> deleteProcessor) {
		LOGGER.trace("Creating a new replication definition processor.");

		this.storeName = storeName;
		this.updateProcessor = updateProcessor;
		this.deleteProcessor = deleteProcessor;
	}

	@Override
	public void close() {

	}

	public Optional<ReplicationDefinition> get(String key) {
		LOGGER.trace("Retreiving replication definition {} from store {}.", key, this);

		return Optional.ofNullable(rateStore.get(key));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		LOGGER.trace("Initializing replication definition state store {} named {}.", this, storeName);

		rateStore = (KeyValueStore<String, ReplicationDefinition>) context.getStateStore(storeName);

		LOGGER.trace("Retreived rate state store {}.", rateStore);

		Flux.fromIterable(() -> rateStore.all()).map(keyValue -> keyValue.value).doOnNext(updateProcessor::onNext)
				.subscribe();
	}

	@Override
	public void process(String key, ReplicationDefinition value) {
		LOGGER.trace("Storing {} in replication definition state store {} with key {}.", value, this, key);

		if (value != null) {
			rateStore.put(key, value);

			updateProcessor.onNext(value);
		} else {
			ReplicationDefinition oldDefinition = rateStore.get(key);

			rateStore.delete(key);

			deleteProcessor.onNext(oldDefinition);
		}

	}
}
