package io.daniellavoie.replication.processor.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class KafkaStreamsMetaServiceImpl implements KafkaStreamsMetaService {
	private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

	private final String protocol;

	public KafkaStreamsMetaServiceImpl(StreamsBuilderFactoryBean streamsBuilderFactoryBean, Environment environment) {
		this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
		this.protocol = isHttpsActive(environment) ? "https" : "http";
	}

	private String buildRemoteStoreEndpoint(StreamsMetadata streamsMetadata) {
		return protocol + "://" + streamsMetadata.host() + ":" + streamsMetadata.port()
				+ ReplicationDefinitionKafkaController.ENDPOINT;
	}

	@Override
	public String findEndpointForKey(String storeName, String key) {
		return buildRemoteStoreEndpoint(getKafkaStreams().metadataForKey(storeName, key, new StringSerializer())) + "/"
				+ key;
	}

	@Override
	public List<String> findEndpointsForStore(String storeName) {
		return getKafkaStreams().allMetadataForStore(storeName).stream()

				.map(this::buildRemoteStoreEndpoint).collect(Collectors.toList());
	}

	private KafkaStreams getKafkaStreams() {
		return streamsBuilderFactoryBean.getKafkaStreams();
	}

	private boolean isHttpsActive(Environment environment) {
		return Arrays.stream(environment.getActiveProfiles()).filter(profile -> "https".contentEquals(profile))
				.findAny().isPresent();
	}

}
