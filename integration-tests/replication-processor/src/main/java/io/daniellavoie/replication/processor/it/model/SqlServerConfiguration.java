package io.daniellavoie.replication.processor.it.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SqlServerConfiguration {
	public enum InsertMode {
		insert, upsert
	}

	private final String connectionUrl;
	private final String user;
	private final String password;
	private final String pkFields;

	@JsonCreator
	public SqlServerConfiguration(@JsonProperty("connectionUrl") String connectionUrl,
			@JsonProperty("user") String user, @JsonProperty("password") String password,
			@JsonProperty("pkFields") String pkFields) {
		this.connectionUrl = connectionUrl;
		this.user = user;
		this.password = password;
		this.pkFields = pkFields;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getPkFields() {
		return pkFields;
	}
}
