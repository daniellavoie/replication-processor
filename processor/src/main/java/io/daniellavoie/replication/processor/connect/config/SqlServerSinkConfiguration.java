package io.daniellavoie.replication.processor.connect.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SinkDefinition;
import io.daniellavoie.replication.processor.model.SinkDefinition.Type;

@Component
public class SqlServerSinkConfiguration implements SinkConnectorConfiguration {
	public static final ConnectorConfigField CONNECTION_URL = new ConnectorConfigField("connection.url",
			"JDBC connection URL.", true, String.class);
	public static final ConnectorConfigField CONNECTION_USER = new ConnectorConfigField("connection.user",
			"JDBC connection user.", true, String.class);
	public static final ConnectorConfigField CONNECTION_PASSWORD = new ConnectorConfigField("connection.password",
			"JDBC connection password.", true, String.class);
	public static final ConnectorConfigField PK_FIELDS = new ConnectorConfigField("pks.fields",
			"Field used to establish the primary key.", true, String.class);

	public static final List<ConnectorConfigField> FIELDS = Arrays.asList(CONNECTION_URL, CONNECTION_USER,
			CONNECTION_PASSWORD, PK_FIELDS);

	@Override
	public String getConnectorClass() {
		return "io.confluent.connect.jdbc.JdbcSinkConnector";
	}

	@Override
	public Optional<String> getKeyConverter() {
		return Optional.empty();
	}

	@Override
	public Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition,
			SinkDefinition sinkDefinition) {
		Map<String, String> defaultConfigs = new HashMap<>();

		defaultConfigs.put("topics", replicationDefinition.getSinkTopic().getName());
		defaultConfigs.put("tasks.max", String.valueOf(sinkDefinition.getTasksMax()));
		defaultConfigs.put("insert.mode", "upsert");
		defaultConfigs.put("pk.mode", "record_value");
		defaultConfigs.put("auto.create", String.valueOf(true));

		return defaultConfigs;
	}

	@Override
	public List<ConnectorConfigField> getFields() {
		return FIELDS;
	}

	@Override
	public Type getSinkType() {
		return Type.SQLSERVER;
	}

}
