package io.daniellavoie.replication.processor.it.attunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
	private final String magic;
	private final String type;
	private final Object headers;
	private final String messageSchemaId;
	private final Object messageSchema;
	private final Message message;

	@JsonCreator
	public Event(@JsonProperty("magic") String magic, @JsonProperty("type") String type,
			@JsonProperty("headers") Object headers, @JsonProperty("messageSchemaId") String messageSchemaId,
			@JsonProperty("messageSchema") Object messageSchema, @JsonProperty("message") Message message) {
		this.magic = magic;
		this.type = type;
		this.headers = headers;
		this.messageSchemaId = messageSchemaId;
		this.messageSchema = messageSchema;
		this.message = message;
	}

	public String getMagic() {
		return magic;
	}

	public String getType() {
		return type;
	}

	public Object getHeaders() {
		return headers;
	}

	public String getMessageSchemaId() {
		return messageSchemaId;
	}

	public Object getMessageSchema() {
		return messageSchema;
	}

	public Message getMessage() {
		return message;
	}
}
