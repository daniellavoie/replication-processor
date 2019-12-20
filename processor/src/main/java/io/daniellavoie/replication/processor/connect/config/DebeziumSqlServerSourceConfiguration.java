package io.daniellavoie.replication.processor.connect.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;

@Component
public class DebeziumSqlServerSourceConfiguration implements SourceConnectorConfiguration {
	public static final ConnectorConfigField DATABASE_HOSTNAME = new ConnectorConfigField("database.hostname",
			"IP address or hostname of the SQL Server database server.", true, String.class);

	public static final ConnectorConfigField DATABASE_PORT = new ConnectorConfigField("database.port",
			"Integer port number of the SQL Server database server. Defaults to 1433", false, Integer.class);

	public static final ConnectorConfigField DATABASE_USER = new ConnectorConfigField("database.user",
			"Username to use when when connecting to the SQL Server database server.", true, String.class);

	public static final ConnectorConfigField DATABASE_PASSWORD = new ConnectorConfigField("database.password",
			"Password to use when when connecting to the SQL Server database server.", true, String.class);

	public static final ConnectorConfigField DATABASE_DBNAME = new ConnectorConfigField("database.dbname",
			"The name of the SQL Server database from which to stream the changes.", true, String.class);

	public static final ConnectorConfigField TABLE_WHITELIST = new ConnectorConfigField("table.whitelist",
			"An optional comma-separated list of regular expressions that match fully-qualified table identifiers for "
					+ "tables to be monitored. Any table not included in the whitelist will be excluded from monitoring. Each "
					+ "identifier is of the form schemaName.tableName. By default the connector will monitor every non-system "
					+ "table in each monitored schema.",
			false, String.class);

	public static final List<ConnectorConfigField> FIELDS = Arrays.asList(DATABASE_HOSTNAME, DATABASE_PORT,
			DATABASE_USER, DATABASE_PASSWORD, DATABASE_DBNAME, TABLE_WHITELIST);

	private final String connectBootstrapServers;

	public DebeziumSqlServerSourceConfiguration(
			@Value("${replication.connect.kafka.bootstrap.servers}") String connectBootstrapServers) {
		this.connectBootstrapServers = connectBootstrapServers;
	}

	@Override
	public List<ConnectorConfigField> getFields() {
		return FIELDS;
	}

	@Override
	public Map<String, String> getDefaultValues(ReplicationDefinition replicationDefinition) {
		Map<String, String> config = new HashMap<>();

		config.put("database.server.name", replicationDefinition.getName());

		config.put("database.history.kafka.bootstrap.servers", connectBootstrapServers);
		config.put("database.history.kafka.topic", "debezium-history-" + replicationDefinition.getName());

		return config;
	}

	@Override
	public String getConnectorClass() {
		return "io.debezium.connector.sqlserver.SqlServerConnector";
	}

	@Override
	public Optional<String> getKeyConverter() {
		return Optional.of("org.apache.kafka.connect.storage.StringConverter");
	}

	@Override
	public Type getSourceType() {
		return Type.SQLSERVER;
	}

	@Override
	public String getValueConverter() {
		return "org.apache.kafka.connect.json.JsonConverter";
	}

	@Override
	public boolean requiresInstance() {
		return true;
	}

}
