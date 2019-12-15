package io.daniellavoie.replication.processor.connect.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectorInstance {
	private final String name;
	private final Map<String, String> config;

	@JsonCreator
	public ConnectorInstance(@JsonProperty("name") String name, @JsonProperty("config") Map<String, String> config) {
		this.name = name;
		this.config = config;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getConfig() {
		return config;
	}
}
