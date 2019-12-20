package io.daniellavoie.replication.processor.it.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SinkDefinition {
	public enum Type {
		SQLSERVER
	}

	private final String name;
	private final Type type;
	private final int tasksMax;
	private final Map<String, String> configs;

	public SinkDefinition(@JsonProperty("name") String name,
			@JsonProperty("type") Type type, @JsonProperty("tasksMax") int tasksMax,
			@JsonProperty("configs") Map<String, String> configs) {
		this.name = name;
		this.type = type;
		this.tasksMax = tasksMax;
		this.configs = configs;
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

	public Map<String, String> getConfigs() {
		return configs;
	}
}
