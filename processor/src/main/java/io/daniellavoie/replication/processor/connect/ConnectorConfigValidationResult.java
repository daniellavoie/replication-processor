package io.daniellavoie.replication.processor.connect;

import java.util.List;

import io.daniellavoie.replication.processor.connect.config.FieldValidationError;

public class ConnectorConfigValidationResult {
	private final boolean valid;
	private final List<FieldValidationError> errors;

	public ConnectorConfigValidationResult(boolean valid, List<FieldValidationError> errors) {
		this.valid = valid;
		this.errors = errors;
	}

	public boolean isValid() {
		return valid;
	}

	public List<FieldValidationError> getErrors() {
		return errors;
	}
}
