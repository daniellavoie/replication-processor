package io.daniellavoie.replication.processor.it.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceDefinition {
	public enum Type {
		TOPIC, ATTUNITY, SQLSERVER
	}

	private final Type type;
	private final Format valueFormat;
	private final String schema;

	private final DebeziumSqlServerConfiguration debeziumSqlServerConfiguration;

	@JsonCreator
	public SourceDefinition(@JsonProperty("type") Type type, @JsonProperty("valueFormat") Format valueFormat,
			@JsonProperty("schema") String schema,
			@JsonProperty("debeziumSqlServerConfiguration") DebeziumSqlServerConfiguration debeziumSqlServerConfiguration) {
		this.type = type;
		this.valueFormat = valueFormat;
		this.schema = schema;
		this.debeziumSqlServerConfiguration = debeziumSqlServerConfiguration;
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

	public DebeziumSqlServerConfiguration getDebeziumSqlServerConfiguration() {
		return debeziumSqlServerConfiguration;
	}
}
