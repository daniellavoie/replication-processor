package io.daniellavoie.replication.processor.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
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
		WebClient.Builder builder = WebClient.builder().baseUrl(connectConfiguration.getUrl());

		if (!connectConfiguration.getBasicUser().trim().equals("")
				&& !connectConfiguration.getBasicPassword().trim().equals("")) {
			builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(
					(connectConfiguration.getBasicUser().trim() + ":" + connectConfiguration.getBasicPassword().trim())
							.getBytes()));
		}

		return WebClient.create(connectConfiguration.getUrl());
	}
}
