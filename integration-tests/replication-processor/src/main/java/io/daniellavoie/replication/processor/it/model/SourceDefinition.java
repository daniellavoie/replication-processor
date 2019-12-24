package io.daniellavoie.replication.processor.it.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceDefinition {
	public enum Type {
		TOPIC, ATTUNITY, MYSQL, SQLSERVER
	}

	private final Type type;
	private final String schema;
	private final Map<String, String> configs;

	@JsonCreator
	public SourceDefinition(@JsonProperty("type") Type type, @JsonProperty("schema") String schema,
			@JsonProperty("configs") Map<String, String> configs) {
		this.type = type;
		this.schema = schema;
		this.configs = configs;
	}

	public Type getType() {
		return type;
	}

	public String getSchema() {
		return schema;
	}

	public Map<String, String> getConfigs() {
		return configs;
	}
}
