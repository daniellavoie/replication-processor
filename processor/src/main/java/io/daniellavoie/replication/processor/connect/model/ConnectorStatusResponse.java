package io.daniellavoie.replication.processor.connect.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectorStatusResponse {
	private final String name;
	private final ConnectorStatus connector;
	private List<TaskStatus> tasks;

	@JsonCreator
	public ConnectorStatusResponse(@JsonProperty("name") String name,
			@JsonProperty("connector") ConnectorStatus connector, @JsonProperty("tasks") List<TaskStatus> tasks) {
		this.name = name;
		this.connector = connector;
		this.tasks = tasks;
	}

	public List<TaskStatus> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskStatus> tasks) {
		this.tasks = tasks;
	}

	public String getName() {
		return name;
	}

	public ConnectorStatus getConnector() {
		return connector;
	}
}
