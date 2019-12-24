package io.daniellavoie.replication.processor.it.source.debezium;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.daniellavoie.replication.processor.it.model.SourceDefinition.Type;
import io.daniellavoie.replication.processor.it.topic.MysqlSourceConfiguration;
import io.daniellavoie.replication.processor.it.topic.TopicConfiguration;

@SpringBootTest(properties = "spring.profiles.active=mysql")
public class AssertMysqlReplicationTest extends AbstractDebeziumTest {
	@Autowired
	private MysqlSourceConfiguration mysqlSourceConfiguration;

	@Override
	Map<String, String> getConfigs() {
		Map<String, String> configs = new HashMap<>();

		
		configs.put("database.hostname", environment.getProperty("replication.source.mysql.hostname"));
		configs.put("database.server.id", environment.getProperty("replication.source.mysql.server-id"));
		configs.put("database.port", environment.getProperty("replication.source.mysql.port"));
		configs.put("database.user", environment.getProperty("replication.source.mysql.user"));
		configs.put("database.password", environment.getProperty("replication.source.mysql.password"));
		configs.put("database.whitelist", environment.getProperty("replication.source.mysql.whitelist"));

		return configs;
	}
	
	@Override
	String getReplicationDefinitionName() {
		return "test-mysql";
	}

	@Override
	TopicConfiguration getSourceTopicConfiguration() {
		return mysqlSourceConfiguration;
	}

	@Override
	Type getSourceType() {
		return Type.MYSQL;
	}
}
