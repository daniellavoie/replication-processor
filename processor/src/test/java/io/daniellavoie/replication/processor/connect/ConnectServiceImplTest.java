package io.daniellavoie.replication.processor.connect;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;

public class ConnectServiceImplTest {
	private ConnectServiceImpl connectService = new ConnectServiceImpl(null);

	@Test
	public void assertIdenticalConfiguration() {
		Map<String, String> newConfig = new HashMap<>();
		newConfig.put("config1", "value1");
		newConfig.put("config2", "value2");
		newConfig.put("config3", "value3");
		ConnectorInstance connectorInstance = new ConnectorInstance("test-connector", newConfig);

		Map<String, String> oldConfig = new HashMap<>();
		oldConfig.put("config1", "value1");
		oldConfig.put("config2", "value2");
		oldConfig.put("config3", "value3");
		ConnectorInstance existingConnectorInstance = new ConnectorInstance("test-connector", oldConfig);

		Assertions.assertTrue(connectService.configurationMatches(connectorInstance, existingConnectorInstance));
	}

	@Test
	public void assertMismatchingConfiguration() {
		Map<String, String> newConfig = new HashMap<>();
		newConfig.put("config1", "value1");
		newConfig.put("config2", "value2");
		ConnectorInstance connectorInstance = new ConnectorInstance("test-connector", newConfig);

		Map<String, String> oldConfig = new HashMap<>();
		oldConfig.put("config1", "value1");
		oldConfig.put("config2", "value2");
		oldConfig.put("config3", "value3");
		ConnectorInstance existingConnectorInstance = new ConnectorInstance("test-connector", oldConfig);

		Assertions.assertFalse(connectService.configurationMatches(connectorInstance, existingConnectorInstance));
		
		newConfig = new HashMap<>();
		newConfig.put("config1", "value1");
		newConfig.put("config2", "value2");
		newConfig.put("config3", "value3");
		connectorInstance = new ConnectorInstance("test-connector", newConfig);

		oldConfig = new HashMap<>();
		oldConfig.put("config1", "value1");
		oldConfig.put("config2", "value2");
		existingConnectorInstance = new ConnectorInstance("test-connector", oldConfig);

		Assertions.assertFalse(connectService.configurationMatches(connectorInstance, existingConnectorInstance));
		
		newConfig = new HashMap<>();
		newConfig.put("config1", "bad-value");
		newConfig.put("config2", "value2");
		newConfig.put("config3", "value3");
		connectorInstance = new ConnectorInstance("test-connector", newConfig);

		oldConfig = new HashMap<>();
		oldConfig.put("config1", "value1");
		oldConfig.put("config2", "value2");
		newConfig.put("config3", "value3");
		existingConnectorInstance = new ConnectorInstance("test-connector", oldConfig);

		Assertions.assertFalse(connectService.configurationMatches(connectorInstance, existingConnectorInstance));
	}
}
