package io.daniellavoie.replication.processor.it;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ReplicationProcessorConfig {
	@Bean
	public WebClient webClient(@Value("${replication.url}") String replicationProcessorUrl) {
		return WebClient.create(replicationProcessorUrl);
	}
}
