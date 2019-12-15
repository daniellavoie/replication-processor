package io.daniellavoie.replication.processor.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("replication.topics.replication-definition")
public class ReplicationDefinitionConfiguration extends TopicConfiguration {

}
