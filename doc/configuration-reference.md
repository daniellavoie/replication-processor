> [Back to Main Menu](../README.md)

# Configuration reference

## Overwriting the default configurations

Default configuration are intended for local testing with the services provided by the `docker-compose.yml` file. The default configuration can be overwritten for both the Replication Processor and the integrations test suites.

## Configuring the Replication Processor with command line arguments

The Replication Processor is a Spring Boot application. As such, you can configure your Kafka bootstrap server using Spring Boot properties.

```
$ java -jar processor/target/replication-processor.jar \
  --spring.kafka.bootstrap-servers=my-kafka-bootstrap-server:9092
  --spring.kafka.properties.sasl.mechanism=PLAIN
  --spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"my-kafka-sasl-key\" password=\"my-kafka-sasl-secret\";
  --spring.kafka.properties.security.protocol=SASL_SSL
  --spring.kafka.properties.ssl.endpoint.identification.algorithm=https
  --spring.kafka.streams.replication-factor=3
  --spring.kafka.properties.basic.auth.credentials.source=USER_INFO
  --spring.kafka.properties.schema.registry.url=https://my-schema-registry-server
  --spring.kafka.properties.schema.registry.basic.auth.user.info=my-schema-registry-key:my-schema-registry-secret
  --connect.url=https://my-connect-rest-url
  --connect.basic.user=my-connect-user
  --connect.basic.password=my-connect-password
```

## Configuring the Replication Processor with environment variables

Being a Spring Boot application, environment variables will be picked up by the Replication Processor.

```
$ export SPRING_KAFKA_BOOTSTRAPSERVERS=my-kafka-bootstrap-server:9092
$ export SPRING_KAFKA_PROPERTIES_SASL_MECHANISM=PLAIN
$ export SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"my-kafka-sasl-key\" password=\"my-kafka-sasl-secret\";
$ export SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL_SASL_SSL
$ export SPRING_KAFKA_PROPERTIES_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=https
$ export SPRING_KAFKA_STREAMS_REPLICATIONFACTORY=3
$ export SPRING_KAFKA_PROPERTIES_BASIC_AUTH_CREDENTIALS_SOURCE=USER_INFO
$ export SPRING_KAFKA_PROPERTIES_SCHEMA_REGISTRY_URL=https://my-schema-registry-server
$ export SPRING_KAFKA_PROPERTIES_REGISTRY_BASIC_AUTH_USER_INFO=my-schema-registry-key:my-schema-registry-secret
$ export CONNECT_URL=https://my-connect-rest-url
$ export CONNECT_BASIC_USER=my-connect-user
$ export CONNECT_BASIC_PASSWORD=my-connect-password

$ java -jar processor/target/replication-processor.jar
```

## Overwriting the configurations for the integration tests

Integration tests are available within `integration-tests/replication-processor` and runs with maven. The test suite runs as a Spring Boot Test project. As such, environment variables can be used to run the tests against a different environment than your local one.

This test suite expects a SqlServer instance to validate that the source data as been successfully replicated. The tests will setup the schema automatically given that the service account has the priviledges to create required tables.

### !Warning!

The integration tests will delete any existing data in the `PricingPublish` table of your sink SqlServer database.

``` 
$ export SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433
$ export SPRING_DATASORUCE_USERNAME=SA
$ export SPRING_DATASOURCE_PASSWORD=Password!

$ export SPRING_KAFKA_BOOTSTRAPSERVERS=my-kafka-bootstrap-server:9092
$ export SPRING_KAFKA_PROPERTIES_SASL_MECHANISM=PLAIN
$ export SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"my-kafka-sasl-key\" password=\"my-kafka-sasl-secret\";
$ export SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL_SASL_SSL
$ export SPRING_KAFKA_PROPERTIES_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=https
$ export SPRING_KAFKA_STREAMS_REPLICATIONFACTORY=3
$ export SPRING_KAFKA_PROPERTIES_BASIC_AUTH_CREDENTIALS_SOURCE=USER_INFO
$ export SPRING_KAFKA_PROPERTIES_SCHEMA_REGISTRY_URL=https://my-schema-registry-server
$ export SPRING_KAFKA_PROPERTIES_REGISTRY_BASIC_AUTH_USER_INFO=my-schema-registry-key:my-schema-registry-secret

$ ./mvnw -f integration-tests/replication-processor/pom.xml
```