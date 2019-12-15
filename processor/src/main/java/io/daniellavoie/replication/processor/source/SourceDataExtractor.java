package io.daniellavoie.replication.processor.source;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

public interface SourceDataExtractor {
	GenericRecord extractRecord(byte[] sourceEvent, Schema schema);
}
