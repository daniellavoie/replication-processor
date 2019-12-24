package io.daniellavoie.replication.processor.source;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractConnectorConfiguration implements SourceConnectorConfiguration {
	public enum DateFormat {
		UTC, MILLISECONDS, NANOSECONDS
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConnectorConfiguration.class);
	private static final ObjectMapper OBJECTMAPPER = new ObjectMapper().findAndRegisterModules();
	private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private final DateFormat dateFormat;

	public AbstractConnectorConfiguration() {
		this(DateFormat.UTC);
	}

	public AbstractConnectorConfiguration(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	protected GenericRecord buildRecordFromMap(Map<String, Object> data, Schema schema) {
		GenericRecordBuilder recordBuilder = new GenericRecordBuilder(schema);

		for (Entry<String, Object> entry : data.entrySet()) {

			Field field = schema.getField(entry.getKey());

			Type type = field.schema().getType();
			String logicalType = field.schema().getProp("logicalType");

			if (type != null) {
				if (type.equals(Type.STRING)) {
					recordBuilder.set(field, entry.getValue().toString());
				} else if (type.equals(Type.INT)) {
					recordBuilder.set(field, Integer.parseInt(entry.getValue().toString()));
				} else if (type.equals(Type.BOOLEAN)) {
					recordBuilder.set(field, (boolean) entry.getValue());
				} else if (type.equals(Type.DOUBLE)) {
					recordBuilder.set(field, (double) entry.getValue());
				} else if (type.equals(Type.LONG)) {
					if ("timestamp-millis".equals(logicalType)) {
						recordBuilder.set(entry.getKey(), convertDate(entry.getValue().toString()).getMillis());
					} else {
						recordBuilder.set(entry.getKey(), (long) (entry.getValue()));
					}
				}
			} else {
				throw new IllegalArgumentException("Schema " + schema + " is invalid.");
			}
		}

		return recordBuilder.build();
	}

	private DateTime convertDate(Object value) {
		if (DateFormat.UTC.equals(dateFormat)) {
			return DateTime.parse(value.toString(), UTC_FORMATTER);
		} else {
			return new Instant(
					Long.valueOf(value.toString()) / (DateFormat.NANOSECONDS.equals(dateFormat) ? 1000000 : 1000))
							.toDateTime();
		}
	}

	protected <T> T readJson(byte[] sourceEvent, Class<T> valueClass) {
		try {
			return OBJECTMAPPER.readValue(sourceEvent, valueClass);
		} catch (IOException e) {
			LOGGER.error("Failed to parse {} to JSON.", new String(sourceEvent));

			throw new RuntimeException(e);
		}
	}

	protected <T> T readJson(byte[] sourceEvent, TypeReference<T> typeReference) {
		try {
			return OBJECTMAPPER.readValue(sourceEvent, typeReference);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
