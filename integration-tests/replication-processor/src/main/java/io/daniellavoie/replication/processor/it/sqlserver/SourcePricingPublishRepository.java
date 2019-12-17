package io.daniellavoie.replication.processor.it.sqlserver;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SourcePricingPublishRepository extends JpaRepository<SourcePricingPublish, String> {

}
