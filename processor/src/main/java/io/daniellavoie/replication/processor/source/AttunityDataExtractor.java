package io.daniellavoie.replication.processor.source;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.replication.processor.attunity.Event;

public class AttunityDataExtractor implements SourceDataExtractor {
	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final ObjectMapper OBJECTMAPPER = new ObjectMapper().findAndRegisterModules();

	@Override
	public GenericRecord extractRecord(byte[] sourceEvent, Schema schema) {
		try {
			Event event = OBJECTMAPPER.readValue(sourceEvent, Event.class);
			GenericRecordBuilder recordBuilder = new GenericRecordBuilder(schema);

			for (Entry<String, Object> entry : event.getMessage().getData().entrySet()) {

				Field field = schema.getField(entry.getKey());

				Type type = field.schema().getType();
				String logicalType = field.schema().getProp("logicalType");

				if (type != null) {
					if (type.equals(Type.STRING)) {
						recordBuilder.set(field, entry.getValue().toString());
					} else if (type.equals(Type.INT)) {
						recordBuilder.set(field, (int) entry.getValue());
					} else if (type.equals(Type.BOOLEAN)) {
						recordBuilder.set(field, (boolean) entry.getValue());
					} else if (type.equals(Type.DOUBLE)) {
						recordBuilder.set(field, (double) entry.getValue());
					} else if (type.equals(Type.LONG)) {
						if ("timestamp-millis".equals(logicalType)) {
							recordBuilder.set(entry.getKey(), parseDateTime(entry.getValue().toString()).getMillis());
						} else {
							recordBuilder.set(entry.getKey(), (long) (entry.getValue()));
						}
					}
				} else {
					throw new IllegalArgumentException("Schema " + schema + " is invalid.");
				}
			}

			return recordBuilder.build();
		} catch (IOException ioEx) {
			throw new RuntimeException(ioEx);
		}
	}

	private DateTime parseDateTime(Object value) {
		return DateTime.parse(value.toString(), FORMATTER);
	}
}
