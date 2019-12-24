package io.daniellavoie.replication.processor.source.debezium;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {
	private final String version;
	private final String connector;
	private final String name;
	private final long tsMs;
	private final String snapshot;
	private final String db;
	private final String schema;
	private final String table;
	private final String changeLsn;
	private final String commitLsn;
	private final String eventSerialNo;

	@JsonCreator
	public Source(@JsonProperty("version") String version, @JsonProperty("connector") String connector,
			@JsonProperty("name") String name, @JsonProperty("ts_ms") long tsMs,
			@JsonProperty("snapshot") String snapshot, @JsonProperty("db") String db,
			@JsonProperty("schema") String schema, @JsonProperty("table") String table,
			@JsonProperty("change_lsn") String changeLsn, @JsonProperty("commit_lsn") String commitLsn,
			@JsonProperty("event_serial_no") String eventSerialNo) {
		this.version = version;
		this.connector = connector;
		this.name = name;
		this.tsMs = tsMs;
		this.snapshot = snapshot;
		this.db = db;
		this.schema = schema;
		this.table = table;
		this.changeLsn = changeLsn;
		this.commitLsn = commitLsn;
		this.eventSerialNo = eventSerialNo;
	}

	public String getVersion() {
		return version;
	}

	public String getConnector() {
		return connector;
	}

	public String getName() {
		return name;
	}

	public long getTsMs() {
		return tsMs;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public String getDb() {
		return db;
	}

	public String getSchema() {
		return schema;
	}

	public String getTable() {
		return table;
	}

	public String getChangeLsn() {
		return changeLsn;
	}

	public String getCommitLsn() {
		return commitLsn;
	}

	public String getEventSerialNo() {
		return eventSerialNo;
	}
}