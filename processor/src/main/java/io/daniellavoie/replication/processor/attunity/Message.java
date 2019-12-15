package io.daniellavoie.replication.processor.attunity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	private final Map<String, Object> beforeData;
	private final Map<String, Object> data;
	private final Map<String, Object> headers;

	@JsonCreator
	public Message(@JsonProperty("beforeData") Map<String, Object> beforeData,
			@JsonProperty("data") Map<String, Object> data, @JsonProperty("headers") Map<String, Object> headers) {
		this.beforeData = beforeData;
		this.data = data;
		this.headers = headers;
	}

	public Map<String, Object> getBeforeData() {
		return beforeData;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}
}
