package io.daniellavoie.replication.processor.source.debezium;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {
	private final Map<String, Object> before;
	private final Map<String, Object> after;
	private final Map<String, String> source;
	private final String op;
	private final long tsMs;

	@JsonCreator
	public Payload(@JsonProperty("before") Map<String, Object> before, @JsonProperty("after") Map<String, Object> after,
			@JsonProperty("source") Map<String, String> source, @JsonProperty("op") String op, @JsonProperty("ts_ms") long tsMs) {
		this.before = before;
		this.after = after;
		this.source = source;
		this.op = op;
		this.tsMs = tsMs;
	}

	public Map<String, Object> getBefore() {
		return before;
	}

	public Map<String, Object> getAfter() {
		return after;
	}

	public Map<String, String> getSource() {
		return source;
	}

	public String getOp() {
		return op;
	}

	public long getTsMs() {
		return tsMs;
	}
}