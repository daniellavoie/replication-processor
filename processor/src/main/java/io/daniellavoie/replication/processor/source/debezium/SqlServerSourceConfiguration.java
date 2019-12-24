package io.daniellavoie.replication.processor.source.debezium;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import io.daniellavoie.replication.processor.config.ConnectConfiguration;
import io.daniellavoie.replication.processor.connect.model.ConnectorConfigField;
import io.daniellavoie.replication.processor.model.SourceDefinition.Type;

@Component
public class SqlServerSourceConfiguration extends DebeziumSourceConfiguration {
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

	public SqlServerSourceConfiguration(ConnectConfiguration connectConfiguration) {
		super(connectConfiguration);
	}

	@Override
	public String getConnectorClass() {
		return "io.debezium.connector.sqlserver.SqlServerConnector";
	}

	@Override
	public List<ConnectorConfigField> getFields() {
		return FIELDS;
	}

	@Override
	public Type getSourceType() {
		return Type.SQLSERVER;
	}
}
