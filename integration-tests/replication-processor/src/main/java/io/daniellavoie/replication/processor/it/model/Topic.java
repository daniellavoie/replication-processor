package io.daniellavoie.replication.processor.it.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Topic {
	private final String name;
	private final boolean compacted;
	private final int partitions;
	private final short replicationFactor;

	@JsonCreator
	public Topic(@JsonProperty("name") String name, @JsonProperty("compacted") boolean compacted,
			@JsonProperty("partitions") int partitions, @JsonProperty("replicationFactor") short replicationFactor) {
		this.name = name;
		this.compacted = compacted;
		this.partitions = partitions;
		this.replicationFactor = replicationFactor;
	}

	public String getName() {
		return name;
	}

	public boolean isCompacted() {
		return compacted;
	}

	public int getPartitions() {
		return partitions;
	}

	public short getReplicationFactor() {
		return replicationFactor;
	}
}
