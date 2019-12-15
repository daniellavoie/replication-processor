package io.daniellavoie.replication.processor.connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskStatus {
	private final String state;
	private final String workerId;

	public TaskStatus(@JsonProperty("state") String state, @JsonProperty("worker_id") String workerId) {
		this.state = state;
		this.workerId = workerId;
	}

	public String getState() {
		return state;
	}

	public String getWorkerId() {
		return workerId;
	}
}
