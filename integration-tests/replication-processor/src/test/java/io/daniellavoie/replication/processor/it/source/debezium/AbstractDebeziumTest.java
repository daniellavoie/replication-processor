package io.daniellavoie.replication.processor.it.source.debezium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.replication.processor.it.AbstractReplicationTest;
import io.daniellavoie.replication.processor.it.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.it.model.SinkDefinition;
import io.daniellavoie.replication.processor.it.model.SourceDefinition;
import io.daniellavoie.replication.processor.it.model.Topic;
import io.daniellavoie.replication.processor.it.sqlserver.SourcePricingPublish;
import io.daniellavoie.replication.processor.it.sqlserver.SourcePricingPublishRepository;
import io.daniellavoie.replication.processor.it.topic.TopicConfiguration;
import reactor.core.publisher.Flux;

public abstract class AbstractDebeziumTest extends AbstractReplicationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDebeziumTest.class);

	@Autowired
	private SourcePricingPublishRepository sourcePricingPublishRepository;

	@Test
	public void assertPricePublishing() throws IOException {
		configureReplicationDefinition();

		LOGGER.info("Pulling any existing and transient records from the sink topic.");

		pullRecords(consumer, Duration.ofSeconds(5)).count().block();

		jdbcTemplate.execute("delete from PricingPublish");

		Flux.range(0, 10000)

				.map(this::generateRandomEvent)

				.buffer(500)

				.flatMap(this::publishEvents)

				.blockLast();

		Assertions.assertEquals(10000, pullRecords(consumer, Duration.ofSeconds(60)).count().block());
		Assertions.assertEquals(10000, pullTableCount("PricingPublish").block());
	}

	private void configureReplicationDefinition() throws IOException {
		String sinkSchema = new BufferedReader(
				new InputStreamReader(new ClassPathResource("avro/PricingPublish.avsc").getInputStream())).lines()
						.collect(Collectors.joining("\n"));
		Map<String, String> configs = getConfigs();

		SourceDefinition sourceDefinition = new SourceDefinition(getSourceType(), null, configs);

		SinkDefinition sinkDefinition = new SinkDefinition("jdbc", SinkDefinition.Type.JDBC, 1,
				buildSinkConfigs(environment));

		TopicConfiguration sourceTopicConfiguration = getSourceTopicConfiguration();

		Topic sourceTopic = new Topic(sourceTopicConfiguration.getName(), sourceTopicConfiguration.isCompacted(),
				sourceTopicConfiguration.getPartitions(), sourceTopicConfiguration.getReplicationFactor());

		Topic sinkTopic = new Topic(pricingPublishConfiguration.getName(), pricingPublishConfiguration.isCompacted(),
				pricingPublishConfiguration.getPartitions(), pricingPublishConfiguration.getReplicationFactor());

		ReplicationDefinition replicationDefinition = new ReplicationDefinition(getReplicationDefinitionName(),
				sourceTopic, sourceDefinition, sinkTopic, sinkSchema, Arrays.asList(sinkDefinition));

		LOGGER.info("Replication Definition : {}", new ObjectMapper().writeValueAsString(replicationDefinition));

		webClient.post().uri("/replication-definition").bodyValue(replicationDefinition).exchange().block();
	}

	private SourcePricingPublish generateRandomEvent(int index) {
		return new SourcePricingPublish(UUID.randomUUID().toString(), String.valueOf(index), LocalDateTime.now(),
				Math.abs(RANDOM.nextInt()), Math.abs(RANDOM.nextInt()), Math.abs(RANDOM.nextInt()),
				Math.abs(RANDOM.nextDouble()), LocalDateTime.now(), LocalDateTime.now(), Math.abs(RANDOM.nextInt(255)),
				LocalDateTime.now(), RANDOM.nextBoolean(), LocalDateTime.now());
	}

	abstract Map<String, String> getConfigs();

	abstract String getReplicationDefinitionName();

	abstract TopicConfiguration getSourceTopicConfiguration();

	abstract SourceDefinition.Type getSourceType();

	@Override
	public String getSourceTopic() {
		return getSourceTopicConfiguration().getName();
	}

	@Override
	public String getSinkTopic() {
		return pricingPublishConfiguration.getName();
	}

	private Flux<SourcePricingPublish> publishEvents(List<SourcePricingPublish> sourcePricingPublishList) {
		return Flux.fromIterable(sourcePricingPublishRepository.saveAll(sourcePricingPublishList));
	}

}
