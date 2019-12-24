package io.daniellavoie.replication.processor.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.daniellavoie.replication.processor.config.ConnectConfiguration;
import io.daniellavoie.replication.processor.connect.exception.InvalidConnectorConfigurationException;
import io.daniellavoie.replication.processor.connect.model.ConnectorConfigField;
import io.daniellavoie.replication.processor.connect.model.ConnectorConfigValidationResult;
import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import io.daniellavoie.replication.processor.connect.model.FieldValidationError;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SinkDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition;
import io.daniellavoie.replication.processor.sink.SinkConnectorConfiguration;
import io.daniellavoie.replication.processor.source.SourceConnectorConfiguration;

@Service
public class ConnectorServiceImpl implements ConnectorService {
	private final Map<SourceDefinition.Type, SourceConnectorConfiguration> sourceConnectors;
	private final Map<SinkDefinition.Type, SinkConnectorConfiguration> sinkConnectors;
	private final ConnectConfiguration connectConfiguration;

	public ConnectorServiceImpl(List<SourceConnectorConfiguration> sourceConnectors,
			List<SinkConnectorConfiguration> sinkConnectors, ConnectConfiguration connectConfiguration) {
		this.sourceConnectors = sourceConnectors.stream()
				.collect(Collectors.toMap(SourceConnectorConfiguration::getSourceType, connector -> connector));
		this.sinkConnectors = sinkConnectors.stream()
				.collect(Collectors.toMap(SinkConnectorConfiguration::getSinkType, connector -> connector));
		this.connectConfiguration = connectConfiguration;
	}

	private Map<String, String> buildSinkConnectorInstanceConfigs(SinkConnectorConfiguration connectorConfiguration,
			SinkDefinition sinkDefinition, ReplicationDefinition replicationDefinition) {
		Map<String, String> connectorInstanceConfigs = new HashMap<>();

		connectorInstanceConfigs.putAll(connectorConfiguration.getFields().stream().collect(
				Collectors.toMap(field -> field.getKey(), field -> sinkDefinition.getConfigs().get(field.getKey()))));
		connectorInstanceConfigs.putAll(connectorConfiguration.getDefaultValues(replicationDefinition, sinkDefinition));

		connectorInstanceConfigs.put("connector.class", connectorConfiguration.getConnectorClass());
		connectorInstanceConfigs.put("value.converter", "io.confluent.connect.avro.AvroConverter");
		connectorInstanceConfigs.put("value.converter.schema.registry.url",
				connectConfiguration.getSchemaRegistryUrl());

		if (connectConfiguration.getSchemaRegistryCrendentialsSource() != null
				&& !connectConfiguration.getSchemaRegistryCrendentialsSource().equals("")
				&& connectConfiguration.getSchemaRegistryUserInfo() != null
				&& !connectConfiguration.getSchemaRegistryUserInfo().trim().equals("")) {

			connectorInstanceConfigs.put("value.converter.schema.registry.basic.auth.user.info",
					connectConfiguration.getSchemaRegistryCrendentialsSource());
			connectorInstanceConfigs.put("value.converter.basic.auth.credentials.source",
					connectConfiguration.getSchemaRegistryUserInfo());
		}

		connectorConfiguration.getKeyConverter()
				.ifPresent(keyConverter -> connectorInstanceConfigs.put("key.converter", keyConverter));

		return connectorInstanceConfigs;
	}

	private Map<String, String> buildSourceConnectorInstanceConfigs(SourceConnectorConfiguration connectorConfiguration,
			ReplicationDefinition replicationDefinition) {
		Map<String, String> connectorInstanceConfigs = new HashMap<>();

		connectorInstanceConfigs.putAll(connectorConfiguration.getFields().stream()

				.filter(field -> replicationDefinition.getSource().getConfigs().containsKey(field.getKey()))

				.collect(Collectors.toMap(field -> field.getKey(),
						field -> replicationDefinition.getSource().getConfigs().get(field.getKey()))));

		connectorInstanceConfigs.putAll(connectorConfiguration.getDefaultValues(replicationDefinition));

		connectorInstanceConfigs.put("connector.class", connectorConfiguration.getConnectorClass());
		connectorInstanceConfigs.put("value.converter", connectorConfiguration.getValueConverter());

		return connectorInstanceConfigs;
	}

	@Override
	public Optional<ConnectorInstance> buildSourceConnectorInstance(ReplicationDefinition replicationDefinition) {
		SourceConnectorConfiguration connectorConfiguration = getSourceConnectorConfiguration(
				replicationDefinition.getSource().getType());

		if (!connectorConfiguration.requiresInstance()) {
			return Optional.empty();
		}

		assertConnectorConfig(replicationDefinition.getSource().getConfigs(), connectorConfiguration);

		return Optional.of(new ConnectorInstance(replicationDefinition.getName() + "-source",
				buildSourceConnectorInstanceConfigs(connectorConfiguration, replicationDefinition)));
	}

	@Override
	public List<ConnectorInstance> buildSinkConnectorInstances(ReplicationDefinition replicationDefinition) {
		return replicationDefinition.getSinks().stream()
				.map(sinkDefinition -> buildSinkConnectorInstance(replicationDefinition, sinkDefinition))
				.collect(Collectors.toList());
	}

	private ConnectorInstance buildSinkConnectorInstance(ReplicationDefinition replicationDefinition,
			SinkDefinition sinkDefinition) {
		SinkConnectorConfiguration connectorConfiguration = getSinkConnectorConfiguration(sinkDefinition);

		assertConnectorConfig(sinkDefinition.getConfigs(), connectorConfiguration);

		return new ConnectorInstance(replicationDefinition.getName() + "-" + sinkDefinition.getName() + "-sink",
				buildSinkConnectorInstanceConfigs(connectorConfiguration, sinkDefinition, replicationDefinition));
	}

	private SinkConnectorConfiguration getSinkConnectorConfiguration(SinkDefinition sinkDefinition) {
		return Optional.ofNullable(sinkConnectors.get(sinkDefinition.getType()))
				.orElseThrow(() -> new IllegalArgumentException(
						sinkDefinition.getType() + " is not a supported sink connector type."));
	}

	@Override
	public SourceConnectorConfiguration getSourceConnectorConfiguration(SourceDefinition.Type type) {
		return Optional.ofNullable(sourceConnectors.get(type))
				.orElseThrow(() -> new IllegalArgumentException(type + " is not a supported source connector type."));
	}

	@Override
	public ConnectorConfigValidationResult validateConnectorConfig(Map<String, String> configs,
			ConnectorConfiguration connectorConfiguration) {
		List<FieldValidationError> errors = connectorConfiguration.getFields().stream()
				.map(connectorConfigField -> validateField(connectorConfigField, configs))

				.filter(Optional::isPresent)

				.map(Optional::get)

				.collect(Collectors.toList());

		return new ConnectorConfigValidationResult(errors.isEmpty(), errors);
	}

	private void assertConnectorConfig(Map<String, String> configs, ConnectorConfiguration connectorConfiguration) {
		ConnectorConfigValidationResult validationResult = validateConnectorConfig(configs, connectorConfiguration);

		if (!validationResult.isValid()) {
			throw new InvalidConnectorConfigurationException(validationResult.getErrors().get(0));
		}
	}

	private Optional<FieldValidationError> validateField(ConnectorConfigField field, Map<String, String> configs) {
		String fieldValue = configs.get(field.getKey());

		if ((fieldValue == null || fieldValue.trim().equals("")) && field.isMandatatory()) {
			return Optional.of(new FieldValidationError(field.getKey(), field.getKey() + " is undefined.", null));
		}

		try {
			if (!field.getValueType().equals(String.class)) {
				if (field.getValueType().equals(Integer.class)) {
					Integer.parseInt(fieldValue);
				} else {
					throw new RuntimeException(
							field.getValueType().getClass().getName() + " is not a supported field type.");
				}
			}
		} catch (Exception ex) {
			return Optional.of(new FieldValidationError(field.getKey(),
					"Failed to parse value " + fieldValue + " for field " + field.getKey() + ".", ex.getMessage()));
		}

		return Optional.empty();
	}

}
