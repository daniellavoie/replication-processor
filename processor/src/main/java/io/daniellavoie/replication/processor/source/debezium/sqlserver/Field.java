package io.daniellavoie.replication.processor.source.debezium.sqlserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
	private final String type;
	private final boolean optional;
	private final String name;
	private final String field;
	private final Field[] fields;

	@JsonCreator
	public Field(@JsonProperty("type") String type, @JsonProperty("optional") boolean optional,
			@JsonProperty("name") String name, @JsonProperty("field") String field,
			@JsonProperty("fields") Field[] fields) {
		this.type = type;
		this.optional = optional;
		this.name = name;
		this.field = field;
		this.fields = fields;
	}

	public String getType() {
		return type;
	}

	public boolean isOptional() {
		return optional;
	}

	public String getName() {
		return name;
	}

	public String getField() {
		return field;
	}

	public Field[] getFields() {
		return fields;
	}
}