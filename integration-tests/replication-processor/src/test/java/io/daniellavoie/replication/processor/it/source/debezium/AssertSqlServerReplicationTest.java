package io.daniellavoie.replication.processor.it.source.debezium;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.daniellavoie.replication.processor.it.model.SourceDefinition.Type;
import io.daniellavoie.replication.processor.it.topic.SqlServerSourceConfiguration;
import io.daniellavoie.replication.processor.it.topic.TopicConfiguration;

@SpringBootTest(properties = "spring.profiles.active=sqlserver")
public class AssertSqlServerReplicationTest extends AbstractDebeziumTest {
	@Autowired
	private SqlServerSourceConfiguration sqlServerSourceConfiguration;

	@Override
	Map<String, String> getConfigs() {
		Map<String, String> configs = new HashMap<>();

		configs.put("database.hostname", environment.getProperty("replication.source.sql-server.hostname"));
		configs.put("database.port", environment.getProperty("replication.source.sql-server.port"));
		configs.put("database.user", environment.getProperty("replication.source.sql-server.username"));
		configs.put("database.password", environment.getProperty("replication.source.sql-server.password"));
		configs.put("database.dbname", environment.getProperty("replication.source.sql-server.name"));

		return configs;
	}
	
	@Override
	String getReplicationDefinitionName() {
		return "test-sqlserver";
	}

	@Override
	TopicConfiguration getSourceTopicConfiguration() {
		return sqlServerSourceConfiguration;
	}
	
	@Override
	Type getSourceType() {
		return Type.SQLSERVER;
	}
}
