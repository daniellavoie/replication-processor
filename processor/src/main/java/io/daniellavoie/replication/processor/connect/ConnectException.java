package io.daniellavoie.replication.processor.connect;

public class ConnectException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final ConnectErrorResponse error;

	public ConnectException(ConnectErrorResponse error, Throwable cause) {
		super(error.getMessage(), cause);
		
		this.error = error;
	}

	public ConnectErrorResponse getError() {
		return error;
	}

}
