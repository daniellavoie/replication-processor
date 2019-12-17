package io.daniellavoie.replication.processor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SinkDefinition {
	public enum Type {
		SQLSERVER
	}

	private final String name;
	private final Type type;
	private final int tasksMax;
	private final SqlServerSinkConfiguration sqlServerSinkConfiguration;

	public SinkDefinition(@JsonProperty("name") String name,
			@JsonProperty("type") Type type, @JsonProperty("tasksMax") int tasksMax,
			@JsonProperty("sqlServerSinkConfiguration") SqlServerSinkConfiguration sqlServerSinkConfiguration) {
		this.name = name;
		this.type = type;
		this.tasksMax = tasksMax;
		this.sqlServerSinkConfiguration = sqlServerSinkConfiguration;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public int getTasksMax() {
		return tasksMax;
	}

	public SqlServerSinkConfiguration getSqlServerSinkConfiguration() {
		return sqlServerSinkConfiguration;
	}
}
