package io.daniellavoie.replication.processor.connect.config;

public class InvalidConnectorConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final FieldValidationError fieldValidationError;

	public InvalidConnectorConfigurationException(FieldValidationError fieldValidationError) {
		this.fieldValidationError = fieldValidationError;
	}

	public FieldValidationError getFieldValidationError() {
		return fieldValidationError;
	}
}
