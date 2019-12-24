package io.daniellavoie.replication.processor.connect;

import java.util.List;
import java.util.Optional;

import io.daniellavoie.replication.processor.connect.model.ConnectorConfigField;

public interface ConnectorConfiguration {
	String getConnectorClass();
	
	Optional<String> getKeyConverter();
	
	List<ConnectorConfigField> getFields();
}
