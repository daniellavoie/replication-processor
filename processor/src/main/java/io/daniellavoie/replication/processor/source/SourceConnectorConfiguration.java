package io.daniellavoie.replication.processor.source;

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import io.daniellavoie.replication.processor.connect.ConnectorConfiguration;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;
import reactor.core.publisher.Flux;

public interface SourceConnectorConfiguration extends ConnectorConfiguration {
	Flux<GenericRecord> extractRecord(byte[] sourceEvent, Schema schema);

	Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition);

	Type getSourceType();

	String getValueConverter();

	boolean requiresInstance();

}
