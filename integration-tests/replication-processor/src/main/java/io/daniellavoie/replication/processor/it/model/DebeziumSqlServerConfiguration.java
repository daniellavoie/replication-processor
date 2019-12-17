package io.daniellavoie.replication.processor.it.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DebeziumSqlServerConfiguration {
	private final String hostname;
	private final int port;
	private final String user;
	private final String password;
	private final String dbname;
	private final String serverName;

	@JsonCreator
	public DebeziumSqlServerConfiguration(@JsonProperty("hostname") String hostname, @JsonProperty("port") int port,
			@JsonProperty("user") String user, @JsonProperty("password") String password,
			@JsonProperty("dbname") String dbname, @JsonProperty("serverName") String serverName) {
		this.hostname = hostname;
		this.port = port;
		this.user = user;
		this.password = password;
		this.dbname = dbname;
		this.serverName = serverName;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDbname() {
		return dbname;
	}

	public String getServerName() {
		return serverName;
	}
}
