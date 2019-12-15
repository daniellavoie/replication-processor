package io.daniellavoie.replication.processor.connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectorStatus {
	private final int id;
	private final String state;
	private final String workerId;
	private final String trace;

	public ConnectorStatus(@JsonProperty("id") int id, @JsonProperty("state") String state,
			@JsonProperty("worker_id") String workerId, @JsonProperty("trace") String trace) {
		this.id = id;
		this.state = state;
		this.workerId = workerId;
		this.trace = trace;
	}

	public int getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public String getWorkerId() {
		return workerId;
	}

	public String getTrace() {
		return trace;
	}
}
