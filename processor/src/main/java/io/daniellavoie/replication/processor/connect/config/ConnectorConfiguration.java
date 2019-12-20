package io.daniellavoie.replication.processor.connect.config;

import java.util.List;
import java.util.Optional;

public interface ConnectorConfiguration {
	String getConnectorClass();
	
	Optional<String> getKeyConverter();
	
	List<ConnectorConfigField> getFields();
}
