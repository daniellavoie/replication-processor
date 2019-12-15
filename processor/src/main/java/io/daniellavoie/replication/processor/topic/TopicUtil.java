package io.daniellavoie.replication.processor.topic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.daniellavoie.replication.processor.model.Topic;

public abstract class TopicUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(TopicUtil.class);

	public static void createTopicIfMissing(TopicConfiguration topicConfiguration, AdminClient adminClient) {
		createTopicIfMissing(topicConfiguration.getName(), topicConfiguration.isCompacted(),
				topicConfiguration.getPartitions(), topicConfiguration.getReplicationFactor(), adminClient);
	}

	public static void createTopicIfMissing(Topic topic, AdminClient adminClient) {
		createTopicIfMissing(topic.getName(), topic.isCompacted(), topic.getPartitions(), topic.getReplicationFactor(),
				adminClient);
	}

	public static void createTopicIfMissing(String name, boolean compacted, int partitions, short replicationFactor,
			AdminClient adminClient) {
		try {
			if (!adminClient.listTopics().names().get().stream().filter(existingTopic -> existingTopic.equals(name))
					.findAny().isPresent()) {
				LOGGER.info("Creating topic {}.", name);

				NewTopic topic = new NewTopic(name, partitions, replicationFactor);

				topic.configs(new HashMap<>());
				topic.configs().put(TopicConfig.CLEANUP_POLICY_CONFIG,
						compacted ? TopicConfig.CLEANUP_POLICY_COMPACT : TopicConfig.CLEANUP_POLICY_DELETE);

				adminClient.createTopics(Arrays.asList(topic)).all().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
