package io.daniellavoie.replication.processor.connect;

import io.daniellavoie.replication.processor.connect.model.ConnectorInstance;
import reactor.core.publisher.Mono;

public interface ConnectService {
	Mono<Void> refreshConnector(ConnectorInstance connectorInstance);
}
