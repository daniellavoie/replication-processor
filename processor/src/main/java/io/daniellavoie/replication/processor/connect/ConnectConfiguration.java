package io.daniellavoie.replication.processor.connect;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("connect")
public class ConnectConfiguration {
	private String url = "http://localhost:8083";

	@Value("${connect.basic.user:}")
	private String basicUser = "";

	@Value("${connect.basic.password:}")
	private String basicPassword = "";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBasicUser() {
		return basicUser;
	}

	public void setBasicUser(String basicUser) {
		this.basicUser = basicUser;
	}

	public String getBasicPassword() {
		return basicPassword;
	}

	public void setBasicPassword(String basicPassword) {
		this.basicPassword = basicPassword;
	}
}
