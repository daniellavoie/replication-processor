package io.daniellavoie.replication.processor.source.debezium.sqlserver;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.daniellavoie.replication.processor.source.AbstractDataExtractor;
import reactor.core.publisher.Flux;

public class DebeziumSqlServerDataExtractor extends AbstractDataExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumSqlServerDataExtractor.class);

	public DebeziumSqlServerDataExtractor() {
		super(DateFormat.NANOSECONDS);
	}

	@Override
	public Flux<GenericRecord> extractRecord(byte[] sourceEvent, Schema schema) {
		return Flux.just(readJson(sourceEvent, Event.class))

				.filter(this::filterMisingPayload)

				.map(event -> buildRecordFromMap(event.getPayload().getAfter(), schema));
	}

	public boolean filterMisingPayload(Event event) {
		if (event.getPayload() == null) {
			LOGGER.warn("Payload is missing from Debezium event.");

			return false;

		} else if (event.getPayload().getAfter() == null) {
			LOGGER.warn("After struct is missing from Debezium event payload.");
			return false;
		}

		return true;
	}
}
