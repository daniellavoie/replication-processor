package io.daniellavoie.replication.processor.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import io.daniellavoie.replication.processor.connect.ConnectConfiguration;

@Configuration
public class KafkaConfig {
	@Bean
	public AdminClient adminClient(KafkaProperties kafkaProperties) {
		return AdminClient.create(kafkaProperties.buildAdminProperties());
	}

	@Bean
	public WebClient webClient(ConnectConfiguration connectConfiguration) {
		return WebClient.create(connectConfiguration.getUrl());
	}
}
