package io.daniellavoie.replication.processor.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReplicationDefinition {
	private final String name;
	private final Topic sourceTopic;
	private final SourceDefinition source;
	private final Topic sinkTopic;
	private final String sinkSchema;
	private final List<SinkDefinition> sinks;

	@JsonCreator
	public ReplicationDefinition(@JsonProperty("name") String name, @JsonProperty("sourceTopic") Topic sourceTopic,
			@JsonProperty("source") SourceDefinition source, @JsonProperty("sinkTopic") Topic sinkTopic,
			@JsonProperty("sinkSchema") String sinkSchema, @JsonProperty("sinks") List<SinkDefinition> sinks) {
		this.name = name;
		this.sourceTopic = sourceTopic;
		this.source = source;
		this.sinkTopic = sinkTopic;
		this.sinkSchema = sinkSchema;
		this.sinks = sinks;
	}

	public String getName() {
		return name;
	}

	public Topic getSourceTopic() {
		return sourceTopic;
	}

	public SourceDefinition getSource() {
		return source;
	}

	public Topic getSinkTopic() {
		return sinkTopic;
	}

	public String getSinkSchema() {
		return sinkSchema;
	}

	public List<SinkDefinition> getSinks() {
		return sinks;
	}
}
