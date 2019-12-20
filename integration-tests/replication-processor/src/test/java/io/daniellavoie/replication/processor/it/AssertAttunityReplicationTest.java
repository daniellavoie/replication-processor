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
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.daniellavoie.replication.processor.it.attunity.Event;
import io.daniellavoie.replication.processor.it.attunity.Message;
import io.daniellavoie.replication.processor.it.model.ReplicationDefinition;
import io.daniellavoie.replication.processor.it.model.SinkDefinition;
import io.daniellavoie.replication.processor.it.model.SourceDefinition;
import io.daniellavoie.replication.processor.it.model.Topic;
import io.daniellavoie.replication.processor.it.topic.AttunitySourceConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AssertAttunityReplicationTest extends AbstractReplicationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssertAttunityReplicationTest.class);

	private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@Autowired
	private AttunitySourceConfiguration attunitySourceConfiguration;

	private KafkaTemplate<byte[], Event> pricingPublishSourceTemplate;

	@BeforeEach
	public void setup() {
		pricingPublishSourceTemplate = new KafkaTemplate<byte[], Event>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
	}

	@Test
	public void assertPricePublishing() throws IOException {
		configureReplicationDefinition();

		LOGGER.info("Pulling any existing and transient records from the sink topic.");
		
		pullRecords(consumer).count().block();

		jdbcTemplate.execute("delete from PricingPublish");

		// Generate events
		Flux.range(0, 10000)

				.map(this::generateRandomEvent)

				.flatMap(this::publishEvent)

				.blockLast();

		Assertions.assertEquals(10000, pullRecords(consumer, Duration.ofSeconds(30)).count().block());
		Assertions.assertEquals(10000, pullTableCount("PricingPublish").block());
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

		SourceDefinition sourceDefinition = new SourceDefinition(SourceDefinition.Type.ATTUNITY, null, null);

		SinkDefinition sinkDefinition = new SinkDefinition("sql-server", SinkDefinition.Type.SQLSERVER, 1,
				buildSinkConfigs(environment));

		ReplicationDefinition replicationDefinition = new ReplicationDefinition("test",
				new Topic(attunitySourceConfiguration.getName(), attunitySourceConfiguration.isCompacted(),
						attunitySourceConfiguration.getPartitions(),
						attunitySourceConfiguration.getReplicationFactor()),
				sourceDefinition,
				new Topic(pricingPublishConfiguration.getName(), pricingPublishConfiguration.isCompacted(),
						pricingPublishConfiguration.getPartitions(),
						pricingPublishConfiguration.getReplicationFactor()),
				sinkSchema, Arrays.asList(sinkDefinition));

		LOGGER.info("Replication Definition : {}", new ObjectMapper().writeValueAsString(replicationDefinition));

		webClient.post().uri("/replication-definition").bodyValue(replicationDefinition).exchange().log().block();
	}

	private Mono<Void> publishEvent(Event event) {
		return Mono.fromFuture(pricingPublishSourceTemplate
				.send(attunitySourceConfiguration.getName(),
						String.valueOf(event.getMessage().getData().get("KeyInstrument")).getBytes(), event)
				.completable()).then();
	}
}
