package io.daniellavoie.replication.processor.it;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import io.daniellavoie.replication.processor.PricingPublish;
import io.daniellavoie.replication.processor.it.model.SqlServerSinkConfiguration;
import io.daniellavoie.replication.processor.it.topic.PricingPublishConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public abstract class AbstractReplicationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReplicationTest.class);

	protected final static Random RANDOM = new Random();

	@Autowired
	protected PricingPublishConfiguration pricingPublishConfiguration;

	@Autowired
	protected KafkaProperties kafkaProperties;

	@Autowired
	protected WebClient webClient;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	protected Environment environment;

	protected Consumer<byte[], PricingPublish> consumer;

	@BeforeEach
	void preSetup() {
		consumer = new DefaultKafkaConsumerFactory<byte[], PricingPublish>(kafkaProperties.buildConsumerProperties())
				.createConsumer();

		consumer.subscribe(Arrays.asList(pricingPublishConfiguration.getName()));
	}

	protected SqlServerSinkConfiguration buildSqlServerSinkConfiguration(Environment environment) {
		return new SqlServerSinkConfiguration(environment.getProperty("replication.sink.sql-server.url"),
				environment.getProperty("replication.sink.sql-server.username"),
				environment.getProperty("replication.sink.sql-server.password"), "KeyEMDPricing");
	}

	protected <K, V> Flux<V> pullRecords(Consumer<K, V> consumer) {
		return Flux.create(subscriber -> {
			boolean completed = false;

			do {
				LOGGER.info("Pulling table count from sink topic.");
				
				ConsumerRecords<K, V> records = consumer.poll(Duration.ofSeconds(30));

				if (records.isEmpty()) {
					completed = true;
					subscriber.complete();
				} else {
					Iterable<ConsumerRecord<K, V>> iterable = () -> records.iterator();

					StreamSupport.stream(iterable.spliterator(), false).map(consumerRecord -> consumerRecord.value())
							.forEach(subscriber::next);
				}
			} while (!completed);
		});
	}

	protected Mono<Long> pullTableCount(String table) {
		return Mono.create(subscriber -> {
			long lastCount = 0;
			long currentCount = 0;
			do {
				lastCount = currentCount;

				LOGGER.info("Pulling table count from {}.", table);

				currentCount = jdbcTemplate.queryForObject("select count(1) from " + table, Long.class);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					subscriber.error(e);
				}
			} while (lastCount != currentCount);

			subscriber.success(lastCount);
		});
	}
}
