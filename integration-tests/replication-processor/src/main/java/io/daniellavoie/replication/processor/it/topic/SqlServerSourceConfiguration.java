package io.daniellavoie.replication.processor.it.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("replication.topics.sqlserver-source")
public class SqlServerSourceConfiguration extends TopicConfiguration {

}
