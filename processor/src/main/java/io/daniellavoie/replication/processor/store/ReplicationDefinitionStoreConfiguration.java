package io.daniellavoie.replication.processor.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("replication.stores.replication-definition")
public class ReplicationDefinitionStoreConfiguration {
	private String name = "replication-definition";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
