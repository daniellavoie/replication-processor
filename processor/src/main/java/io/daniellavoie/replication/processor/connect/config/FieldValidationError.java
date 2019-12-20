package io.daniellavoie.replication.processor.connect.config;

public class FieldValidationError {
	private final String configKey;
	private final String errorDescription;
	private final String cause;
	
	public FieldValidationError(String configKey, String errorDescription, String cause) {
		this.configKey = configKey;
		this.errorDescription = errorDescription;
		this.cause = cause;
	}

	public String getConfigKey() {
		return configKey;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public String getCause() {
		return cause;
	}
}
