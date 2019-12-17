package io.daniellavoie.replication.processor.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.replication.processor.it.model.DebeziumSqlServerConfiguration;
import io.daniellavoie.replication.processor.it.model.Format;
import io.daniellavoie.replication.processor.it.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.it.model.SinkDefinition;
import io.daniellavoie.replication.processor.it.model.SourceDefinition;
import io.daniellavoie.replication.processor.it.model.Topic;
import io.daniellavoie.replication.processor.it.sqlserver.SourcePricingPublish;
import io.daniellavoie.replication.processor.it.sqlserver.SourcePricingPublishRepository;
import io.daniellavoie.replication.processor.it.topic.SqlServerSourceConfiguration;
import reactor.core.publisher.Flux;

@SpringBootTest
public class AssertSqlServerReplicationTest extends AbstractReplicationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssertSqlServerReplicationTest.class);

	@Autowired
	private SqlServerSourceConfiguration sqlServerSourceConfiguration;

	@Autowired
	private SourcePricingPublishRepository SourcePricingPublishRepository;

	@Test
	public void assertPricePublishing() throws IOException {
		configureReplicationDefinition();
		
		// Clean existing records.
		pullRecords(consumer).count().block();

		jdbcTemplate.execute("delete from PricingPublish");

		Flux.range(0, 10000)

				.map(this::generateRandomEvent)

				.buffer(500)

				.flatMap(this::publishEvents)

				.blockLast();

		Assertions.assertEquals(10000, pullRecords(consumer).count().block());
		Assertions.assertEquals(10000, pullTableCount("PricingPublish").block());
	}

	private void configureReplicationDefinition() throws IOException {
		String sinkSchema = new BufferedReader(
				new InputStreamReader(new ClassPathResource("avro/PricingPublish.avsc").getInputStream())).lines()
						.collect(Collectors.joining("\n"));

		DebeziumSqlServerConfiguration debeziumSqlServerConfiguration = new DebeziumSqlServerConfiguration(
				environment.getProperty("replication.source.sql-server.hostname"),
				environment.getProperty("replication.source.sql-server.port", Integer.class),
				environment.getProperty("replication.source.sql-server.username"),
				environment.getProperty("replication.source.sql-server.password"),
				environment.getProperty("replication.source.sql-server.name"),
				environment.getProperty("replication.source.sql-server.hostname"));

		SourceDefinition sourceDefinition = new SourceDefinition(SourceDefinition.Type.SQLSERVER, Format.JSON, null,
				debeziumSqlServerConfiguration);
		SinkDefinition sinkDefinition = new SinkDefinition("sql-server", SinkDefinition.Type.SQLSERVER, 1,
				buildSqlServerSinkConfiguration(environment));

		ReplicationDefinition replicationDefinition = new ReplicationDefinition("test-sqlserver",
				new Topic(sqlServerSourceConfiguration.getName(), sqlServerSourceConfiguration.isCompacted(),
						sqlServerSourceConfiguration.getPartitions(),
						sqlServerSourceConfiguration.getReplicationFactor()),
				sourceDefinition,
				new Topic(pricingPublishConfiguration.getName(), pricingPublishConfiguration.isCompacted(),
						pricingPublishConfiguration.getPartitions(),
						pricingPublishConfiguration.getReplicationFactor()),
				Format.AVRO, sinkSchema, Arrays.asList(sinkDefinition));

		LOGGER.info("Replication Definition : {}", new ObjectMapper().writeValueAsString(replicationDefinition));

		webClient.post().uri("/replication-definition").bodyValue(replicationDefinition).exchange().block();
	}

	private SourcePricingPublish generateRandomEvent(int index) {
		return new SourcePricingPublish(UUID.randomUUID().toString(), String.valueOf(index), LocalDateTime.now(),
				Math.abs(RANDOM.nextInt()), Math.abs(RANDOM.nextInt()), Math.abs(RANDOM.nextInt()),
				Math.abs(RANDOM.nextDouble()), LocalDateTime.now(), LocalDateTime.now(), Math.abs(RANDOM.nextInt(255)),
				LocalDateTime.now(), RANDOM.nextBoolean(), LocalDateTime.now());
	}

	private Flux<SourcePricingPublish> publishEvents(List<SourcePricingPublish> sourcePricingPublishList) {
		return Flux.fromIterable(SourcePricingPublishRepository.saveAll(sourcePricingPublishList));
	}
}
