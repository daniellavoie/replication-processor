package io.daniellavoie.replication.processor.source;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import reactor.core.publisher.Flux;

public interface SourceDataExtractor {
	Flux<GenericRecord> extractRecord(byte[] sourceEvent, Schema schema);
}
