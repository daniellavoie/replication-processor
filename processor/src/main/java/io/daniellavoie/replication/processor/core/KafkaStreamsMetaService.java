package io.daniellavoie.replication.processor.core;

import java.util.List;

public interface KafkaStreamsMetaService {
	List<String> findEndpointsForStore(String storeName);
	
	String findEndpointForKey(String storeName, String key);
}
