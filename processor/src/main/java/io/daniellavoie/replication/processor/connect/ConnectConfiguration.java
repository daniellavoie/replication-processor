package io.daniellavoie.replication.processor.connect;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("connect")
public class ConnectConfiguration {
	private String url = "http://localhost:8083";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
