package io.daniellavoie.replication.processor.connect.config;

public class ConnectorConfigField {
	private final String key;
	private final String description;
	private final boolean mandatatory;
	private final Class<?> valueType;

	public ConnectorConfigField(String key, String description, boolean mandatatory, Class<?> valueType) {
		this.key = key;
		this.description = description;
		this.mandatatory = mandatatory;
		this.valueType = valueType;
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public boolean isMandatatory() {
		return mandatatory;
	}

	public Class<?> getValueType() {
		return valueType;
	}
}
