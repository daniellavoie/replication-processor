package io.daniellavoie.replication.processor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("replication.connect")
public class ConnectConfiguration {
	private String url = "http://localhost:8083";
	private String basicUser = "";
	private String basicPassword = "";
	private String kafkaBootstrapServers = "localhost:9092";
	private String schemaRegistryUrl= "http://localhost:8085";
	private String schemaRegistryCrendentialsSource;
	private String schemaRegistryUserInfo;

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

	public String getKafkaBootstrapServers() {
		return kafkaBootstrapServers;
	}

	public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
		this.kafkaBootstrapServers = kafkaBootstrapServers;
	}

	public String getSchemaRegistryUrl() {
		return schemaRegistryUrl;
	}

	public void setSchemaRegistryUrl(String schemaRegistryUrl) {
		this.schemaRegistryUrl = schemaRegistryUrl;
	}

	public String getSchemaRegistryCrendentialsSource() {
		return schemaRegistryCrendentialsSource;
	}

	public void setSchemaRegistryCrendentialsSource(String schemaRegistryCrendentialsSource) {
		this.schemaRegistryCrendentialsSource = schemaRegistryCrendentialsSource;
	}

	public String getSchemaRegistryUserInfo() {
		return schemaRegistryUserInfo;
	}

	public void setSchemaRegistryUserInfo(String schemaRegistryUserInfo) {
		this.schemaRegistryUserInfo = schemaRegistryUserInfo;
	}
}
