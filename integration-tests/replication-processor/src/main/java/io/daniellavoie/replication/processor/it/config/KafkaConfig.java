package io.daniellavoie.replication.processor.it.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KafkaConfig {
	@Bean
	public AdminClient adminClient(KafkaProperties kafkaProperties) {
		return AdminClient.create(kafkaProperties.buildAdminProperties());
	}
	
	@Bean
	public WebClient connectWebClient(ConnectConfiguration connectConfiguration) {
		WebClient.Builder builder = WebClient.builder().baseUrl(connectConfiguration.getUrl());

		if (!connectConfiguration.getBasicUser().trim().equals("")
				&& !connectConfiguration.getBasicPassword().trim().equals("")) {
			builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(
					(connectConfiguration.getBasicUser().trim() + ":" + connectConfiguration.getBasicPassword().trim())
							.getBytes()));

			builder.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		}

		return WebClient.create(connectConfiguration.getUrl());
	}
}
