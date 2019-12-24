package io.daniellavoie.replication.processor.core;

import io.daniellavoie.replication.processor.model.ReplicationDefinition;

public class ReplicationDefinitionUpdate {
	public enum EventType {
		UPDATED, DELETED
	}
	
	private final String name;
	private final EventType eventType;
	private final ReplicationDefinition newValue;
	
	public ReplicationDefinitionUpdate(String name, EventType eventType, ReplicationDefinition newValue) {
		this.name = name;
		this.eventType = eventType;
		this.newValue = newValue;
	}

	public String getName() {
		return name;
	}

	public EventType getEventType() {
		return eventType;
	}

	public ReplicationDefinition getNewValue() {
		return newValue;
	}
}
