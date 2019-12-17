package io.daniellavoie.replication.processor.source.attunity;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.daniellavoie.replication.processor.source.AbstractDataExtractor;
import reactor.core.publisher.Flux;

public class AttunityDataExtractor extends AbstractDataExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AttunityDataExtractor.class);

	@Override
	public Flux<GenericRecord> extractRecord(byte[] sourceEvent, Schema schema) {
		Event event = readJson(sourceEvent, Event.class);

		if (event.getMessage() == null) {
			LOGGER.warn("Message is missing from Attunity event.");
			return Flux.empty();
		} else if (event.getMessage().getData() == null) {
			LOGGER.warn("Data struct is missing from Attunity event message.");
			return Flux.empty();
		}

		return Flux.just(buildRecordFromMap(readJson(sourceEvent, Event.class).getMessage().getData(), schema));
	}
}