package io.daniellavoie.replication.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class ReplicationProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReplicationProcessorApplication.class, args);
	}

}
