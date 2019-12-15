package io.daniellavoie.replication.processor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceDefinition {
	public enum Type {
		TOPIC, ATTUNITY, SQLSERVER
	}

	private final Type type;
	private final Format valueFormat;
	private final String schema;

	@JsonCreator
	public SourceDefinition(@JsonProperty("type") Type type, @JsonProperty("valueFormat") Format valueFormat,
			@JsonProperty("schema") String schema) {
		this.type = type;
		this.valueFormat = valueFormat;
		this.schema = schema;
	}

	public Type getType() {
		return type;
	}

	public Format getValueFormat() {
		return valueFormat;
	}

	public String getSchema() {
		return schema;
	}
}
