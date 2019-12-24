package io.daniellavoie.replication.processor.connect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectErrorResponse {
	private final int errorCode;
	private final String message;

	@JsonCreator
	public ConnectErrorResponse(@JsonProperty("error_code") int errorCode, @JsonProperty("message") String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}
}
