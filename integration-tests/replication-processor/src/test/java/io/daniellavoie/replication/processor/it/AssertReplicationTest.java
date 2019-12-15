package io.daniellavoie.replication.processor.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.replication.processor.PricingPublish;
import io.daniellavoie.replication.processor.it.attunity.Event;
import io.daniellavoie.replication.processor.it.attunity.Message;
import io.daniellavoie.replication.processor.it.model.Format;
import io.daniellavoie.replication.processor.it.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.it.model.SinkDefinition;
import io.daniellavoie.replication.processor.it.model.SourceDefinition;
import io.daniellavoie.replication.processor.it.model.SqlServerConfiguration;
import io.daniellavoie.replication.processor.it.model.Topic;
import io.daniellavoie.replication.processor.it.topic.PricingPublishConfiguration;
import io.daniellavoie.replication.processor.it.topic.PricingPublishSourceConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class AssertReplicationTest {
	private final static Random RANDOM = new Random();
	private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@Autowired
	private PricingPublishConfiguration pricingPublishConfiguration;

	@Autowired
	private PricingPublishSourceConfiguration pricingPublishSourceConfiguration;

	@Autowired
	private KafkaProperties kafkaProperties;

	@Autowired
	private WebClient webClient;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Environment environment;

	private KafkaTemplate<byte[], Event> pricingPublishSourceTemplate;
	private Consumer<byte[], PricingPublish> consumer;

	@BeforeEach
	public void setup() {
		consumer = new DefaultKafkaConsumerFactory<byte[], PricingPublish>(kafkaProperties.buildConsumerProperties())
				.createConsumer();

		consumer.subscribe(Arrays.asList(pricingPublishConfiguration.getName()));

		pricingPublishSourceTemplate = new KafkaTemplate<byte[], Event>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));

	}

	@Test
	public void assertPricePublishing() throws IOException {
		configureReplicationDefinition();

		jdbcTemplate.execute("delete from PricingPublish");

		CountDownLatch latch = new CountDownLatch(10000);

		Flux.range(0, 10000)

				.map(this::generateRandomEvent)

				.flatMap(this::publishEvent)

				.blockLast();

		boolean empty = true;
		int emptyPollCount = 0;
		do {
			ConsumerRecords<byte[], PricingPublish> records = consumer.poll(Duration.ofSeconds(20));
			empty = records.isEmpty();

			if (empty) {
				++emptyPollCount;
			}

			Flux.fromIterable(() -> records.iterator())

					.doOnNext(consumerRecord -> latch.countDown()).then().block();
		} while (latch.getCount() != 10000l && !empty && emptyPollCount < 3);

		Assertions.assertEquals(0, latch.getCount());

		Assertions.assertEquals(10000,
				jdbcTemplate.queryForObject("select count(1) from PricingPublish", Integer.class));
	}

	private Event generateRandomEvent(int index) {
		Map<String, Object> data = new HashMap<>();

		data.put("KeyEMDPricing", UUID.randomUUID().toString());
		data.put("KeyInstrument", index);
		data.put("InstrumentValueAsOf", LocalDateTime.now().format(FORMATTER));
		data.put("KeyInstrumentSource", Math.abs(RANDOM.nextInt()));
		data.put("KeyInstrumentLocation", Math.abs(RANDOM.nextInt()));
		data.put("KeyInstrumentMeasure", Math.abs(RANDOM.nextInt()));
		data.put("InstrumentValue", Math.abs(RANDOM.nextDouble()));
		data.put("InstrumentDeliveryStart", LocalDateTime.now().format(FORMATTER));
		data.put("InstrumentDeliveryEnd", LocalDateTime.now().format(FORMATTER));
		data.put("UpdOperation", Math.abs(RANDOM.nextInt(255)));
		data.put("UpdDate", LocalDateTime.now().format(FORMATTER));
		data.put("ActiveRow", RANDOM.nextBoolean());
		data.put("InstrumentValuePublishDate", LocalDateTime.now().format(FORMATTER));

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("operation", "INSERT");
		headers.put("changeSequence", "20191111091304130000000000000000149");
		headers.put("timestamp", "2019-11-11T09:13:04.067");
		headers.put("streamPosition", "000dac8c:000fc98a:0002");
		headers.put("transactionId", "00000000000000000000000113470F30");
		headers.put("changeMask", "1F903C3000003FFFFFFFFFFFFFFFFF");
		headers.put("columnMask", "1F903C3000003FFFFFFFFFFFFFFFFF");
		headers.put("transactionEventCounter", 1);
		headers.put("transactionLastEvent", true);

		return new Event("magic-stuff", "some-type", null, null, null, new Message(null, data, headers));
	}

	private void configureReplicationDefinition() throws IOException {
		String sinkSchema = new BufferedReader(
				new InputStreamReader(new ClassPathResource("avro/PricingPublish.avsc").getInputStream())).lines()
						.collect(Collectors.joining("\n"));

		String connectionUrl = environment.getProperty("replication.replicated-db-url") + ";user="
				+ environment.getProperty("spring.datasource.username") + ";password="
				+ environment.getProperty("spring.datasource.password");

		SqlServerConfiguration sqlServerConfiguration = new SqlServerConfiguration(connectionUrl,
				environment.getProperty("spring.datasource.username"),
				environment.getProperty("spring.datasource.password"), "KeyEMDPricing");

		SourceDefinition sourceDefinition = new SourceDefinition(SourceDefinition.Type.ATTUNITY, Format.JSON, null);
		SinkDefinition sinkDefinition = new SinkDefinition("sql-server", SinkDefinition.Type.SQLSERVER, 1,
				sqlServerConfiguration);

		ReplicationDefinition replicationDefinition = new ReplicationDefinition("test",
				new Topic(pricingPublishSourceConfiguration.getName(), pricingPublishSourceConfiguration.isCompacted(),
						pricingPublishSourceConfiguration.getPartitions(),
						pricingPublishSourceConfiguration.getReplicationFactor()),
				sourceDefinition,
				new Topic(pricingPublishConfiguration.getName(), pricingPublishConfiguration.isCompacted(),
						pricingPublishConfiguration.getPartitions(),
						pricingPublishConfiguration.getReplicationFactor()),
				Format.AVRO, sinkSchema, Arrays.asList(sinkDefinition));

		System.out.println(new ObjectMapper().writeValueAsString(replicationDefinition));

		webClient.post().uri("/replication-definition").bodyValue(replicationDefinition).exchange().log().block();
	}

	private Mono<Void> publishEvent(Event event) {
		return Mono.fromFuture(pricingPublishSourceTemplate
				.send(pricingPublishSourceConfiguration.getName(),
						String.valueOf(event.getMessage().getData().get("KeyInstrument")).getBytes(), event)
				.completable()).then();
	}
}
