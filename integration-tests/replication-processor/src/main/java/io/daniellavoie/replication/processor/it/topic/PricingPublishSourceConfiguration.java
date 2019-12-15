package io.daniellavoie.replication.processor.it.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("replication.topics.pricing-publish-source")
public class PricingPublishSourceConfiguration extends TopicConfiguration {

}
