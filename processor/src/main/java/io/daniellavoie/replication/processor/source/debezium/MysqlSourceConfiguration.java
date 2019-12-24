package io.daniellavoie.replication.processor.source.debezium;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.daniellavoie.replication.processor.config.ConnectConfiguration;
import io.daniellavoie.replication.processor.connect.model.ConnectorConfigField;
import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;

@Component
public class MysqlSourceConfiguration extends DebeziumSourceConfiguration {
	private static final ConnectorConfigField DATABASE_HOSTNAME = new ConnectorConfigField("database.hostname", "",
			true, String.class);
	private static final ConnectorConfigField DATABASE_SERVER_ID = new ConnectorConfigField("database.server.id", "",
			true, Integer.class);
	private static final ConnectorConfigField DATABASE_PORT = new ConnectorConfigField("database.port", "", true,
			Integer.class);
	private static final ConnectorConfigField DATABASE_USER = new ConnectorConfigField("database.user", "", true,
			String.class);
	private static final ConnectorConfigField DATABASE_PASSWORD = new ConnectorConfigField("database.password", "",
			true, String.class);
	private static final ConnectorConfigField DATABASE_WHITELIST = new ConnectorConfigField("database.whitelist", "",
			true, String.class);

	private static final List<ConnectorConfigField> FIELDS = Arrays.asList(DATABASE_HOSTNAME, DATABASE_SERVER_ID,
			DATABASE_PORT, DATABASE_USER, DATABASE_PASSWORD, DATABASE_WHITELIST);

	public MysqlSourceConfiguration(ConnectConfiguration connectConfiguration) {
		super(connectConfiguration);
	}

	@Override
	public Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition) {
		Map<String, String> defaultValues = super.getDefaultValues(replicationDefinition);

		defaultValues.put("include.schema.changes", "false");

		return defaultValues;
	}

	@Override
	public Type getSourceType() {
		return Type.MYSQL;
	}

	@Override
	public String getConnectorClass() {
		return "io.debezium.connector.mysql.MySqlConnector";
	}

	@Override
	public List<ConnectorConfigField> getFields() {
		return FIELDS;
	}

}
