package io.daniellavoie.replication.processor.it;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import io.daniellavoie.replication.processor.PricingPublish;
import io.daniellavoie.replication.processor.it.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.it.topic.PricingPublishConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@SpringBootTest
public abstract class AbstractReplicationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReplicationTest.class);

	protected final static Random RANDOM = new Random();

	@Autowired
	protected PricingPublishConfiguration pricingPublishConfiguration;

	@Autowired
	protected AdminClient adminClient;

	@Autowired
	protected KafkaProperties kafkaProperties;

	@Autowired
	protected WebClient webClient;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	protected Environment environment;

	@Autowired
	private WebClient connectWebClient;

	protected Consumer<byte[], PricingPublish> consumer;

	@BeforeEach
	void preSetup() {
		adminClient.deleteTopics(Arrays.asList(getSinkTopic(), getSourceTopic()));

		LOGGER.info("Cleaning existing replication definitions.");

		webClient.get().uri("/replication-definition").exchange()
				.flatMapMany(response -> response.bodyToFlux(ReplicationDefinition.class))

				.map(ReplicationDefinition::getName)

				.flatMap(name -> webClient.delete().uri("/replication-definition/" + name).exchange()

						.doOnNext(response -> LOGGER.info("Deleted replication definition {}.", name))

						.then())

				.blockLast();

		LOGGER.info("Cleaning existing connectors on the connect server.");

		connectWebClient.get().uri("/connectors").exchange()

				.flatMapMany(response -> response.bodyToMono(new ParameterizedTypeReference<String[]>() {
				}))

				.flatMap(connectors -> Flux.fromArray(connectors))

				.flatMap(connectorName -> connectWebClient.delete().uri("/connectors/" + connectorName).exchange()
						.doOnNext(response -> LOGGER.info("Deleted connector {}.", connectorName)))

				.blockLast();

		consumer = new DefaultKafkaConsumerFactory<byte[], PricingPublish>(kafkaProperties.buildConsumerProperties())
				.createConsumer();

		consumer.subscribe(Arrays.asList(pricingPublishConfiguration.getName()));
	}

	@AfterEach
	void postSetup() {
		consumer.close();
	}

	protected Map<String, String> buildSinkConfigs(Environment environment) {
		Map<String, String> configs = new HashMap<>();

		configs.put("connection.url", environment.getProperty("replication.sink.jdbc.url"));
		configs.put("connection.user", environment.getProperty("replication.sink.jdbc.username"));
		configs.put("connection.password", environment.getProperty("replication.sink.jdbc.password"));
		configs.put("pks.fields", "KeyEMDPricing");

		return configs;
	}

	public abstract String getSourceTopic();

	public abstract String getSinkTopic();

	private <K, V> void createRecordPuller(FluxSink<V> subscriber, Consumer<K, V> consumer, Duration pullTimeout,
			AtomicLong recordCount) {
		boolean completed = false;

		do {
			ConsumerRecords<K, V> records = consumer.poll(pullTimeout);

			recordCount.addAndGet(records.count());

			if (records.isEmpty()) {
				completed = true;
				subscriber.complete();
			} else {
				Iterable<ConsumerRecord<K, V>> iterable = () -> records.iterator();

				StreamSupport.stream(iterable.spliterator(), false).map(consumerRecord -> consumerRecord.value())
						.forEach(subscriber::next);
			}
		} while (!completed);
	}

	private void createTableCountPuller(MonoSink<Long> subscriber, String table) {
		long lastCount = 0;
		long currentCount = 0;
		do {
			lastCount = currentCount;

			currentCount = jdbcTemplate.queryForObject("select count(1) from " + table, Long.class);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				subscriber.error(e);
			}
		} while (lastCount != currentCount);

		subscriber.success(lastCount);
	}

	protected <K, V> Flux<V> pullRecords(Consumer<K, V> consumer) {
		return pullRecords(consumer, Duration.ofSeconds(5));
	}

	protected <K, V> Flux<V> pullRecords(Consumer<K, V> consumer, Duration timeout) {
		AtomicLong recordCount = new AtomicLong();

		return Flux.<V>create(subscriber -> createRecordPuller(subscriber, consumer, timeout, recordCount))

				.doOnSubscribe(
						subscriber -> LOGGER.info("Pulling records from {}.", pricingPublishConfiguration.getName()))

				.doOnComplete(() -> LOGGER.info("Pulled {} records.", recordCount.get()));
	}

	protected Mono<Long> pullTableCount(String table) {
		return Mono.<Long>create(subscriber -> createTableCountPuller(subscriber, table))

				.doOnSubscribe(subscriber -> LOGGER.info("Pulling record count from table {}.", table))

				.doOnNext(count -> LOGGER.info("Pulled {} records.", count));
	}
}
