package io.daniellavoie.replication.processor.it.model;

public class SourceDefinition {
	public enum Type {
		TOPIC, ATTUNITY, SQLSERVER
	}

	private final Type type;
	private final Format valueFormat;
	private final String schema;

	public SourceDefinition(Type type, Format valueFormat, String schema) {
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
