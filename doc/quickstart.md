> [Back to Main Menu](../README.md)

# Quickstart

## Pre requisites to run the Replication Processor locally

* Java 8
* Docker and docker-compose (to run the infrastructure for local testing)
* A Kafka cluster
* A Connect cluster
* Schema Registry (Avro is used to do type mapping between the destination and the source)

## Deploying the infrastructure locally

The `docker-compose.yml` from this repository provides all the infrastructure to run locally and testing. A startup script is packaged with the project. It takes care of running the Docker Compose command as well as running post install scripts (eg: configuring a MySQL user with Change Data Capture permissions).

```
$ ./deploy-infrastructure.sh
```

## Building an executable binary

```
$ ./mvnw -f processor/pom.xml clean package
```

## Run the executable binary

```
java -jar processor/target/replication-processor.jar
```

## Create a Replication Definition

```
curl -X POST http://localhost:8080/replication-definition \
  -H "Content-Type: application/json"  \
  --data-binary @- << EOF
{
  "name":"test",
  "sourceTopic":{
    "name":"SourcePricingPublish",
    "compacted":false,
    "partitions":1,
    "replicationFactor":1
  },
  "source":{
    "type":"SQLSERVER",
    "configs": {
      "database.hostname": "sqlserver",
      "database.port": "1433",
      "database.user": "SA",
      "database.password": "Password!",
      "database.dbname": "tests"
    }
  },
  "sinkTopic":{
    "name":"PricingPublish",
    "compacted":false,
    "partitions":1,
    "replicationFactor":1
  },
  "sinks":[
    {
      "type":"SQLSERVER",
      "tasksMax":1,
      "name":"sql-server",
      "configs":{
        "connection.url":"jdbc:sqlserver://sqlserver:1433",
        "connection.user":"SA",
        "connection.password":"Password!",
        "pks.fields":"KeyEMDPricing"
      }
    }
  ],
  "sinkSchema": "\
    {\n\
      \"type\": \"record\",\n\
      \"name\": \"PricingPublish\",\n\
      \"namespace\": \"io.daniellavoie.replication.processor\",\n\
      \"fields\": [\n\
        {\n\
          \"name\": \"KeyEMDPricing\",\n\
          \"type\": {\"type\": \"string\"}\n\
        },\n\
        {\n\
          \"name\": \"KeyInstrument\",\n\
          \"type\": \"int\"\n\
        },\n\
        {\n\
          \"name\": \"InstrumentValueAsOf\",\n\
          \"type\": {\"type\" : \"long\", \"logicalType\" : \"timestamp-millis\"}\n\
        },\n\
        {\n\
          \"name\": \"KeyInstrumentSource\",\n\
          \"type\": \"int\"\n\
        },\n\
        {\n\
          \"name\": \"KeyInstrumentLocation\",\n\
          \"type\": \"int\"\n\
        },\n\
        {\n\
          \"name\": \"KeyInstrumentMeasure\",\n\
          \"type\": \"int\"\n\
        },\n\
        {\n\
          \"name\": \"InstrumentValue\",\n\
          \"type\": \"double\"\n\
        },\n\
        {\n\
          \"name\": \"InstrumentDeliveryStart\",\n\
          \"type\": {\"type\" : \"long\", \"logicalType\" : \"timestamp-millis\"}\n\
        },\n\
        {\n\
          \"name\": \"InstrumentDeliveryEnd\",\n\
          \"type\": {\"type\" : \"long\", \"logicalType\" : \"timestamp-millis\"}\n\
        },\n\
        {\n\
          \"name\": \"UpdOperation\",\n\
          \"type\": \"int\"\n\
        },\n\
        {\n\
          \"name\": \"UpdDate\",\n\
          \"type\": {\"type\" : \"long\", \"logicalType\" : \"timestamp-millis\"}\n\
        },\n\
        {\n\
          \"name\": \"ActiveRow\",\n\
          \"type\": \"boolean\"\n\
        },\n\
        {\n\
          \"name\": \"InstrumentValuePublishDate\",\n\
          \"type\": {\"type\" : \"long\", \"logicalType\" : \"timestamp-millis\"}\n\
        }\n\
      ]\n\
    }"
}

EOF
```

## Inspect Control Center

Control Center can be opened from http://localhost:9021 and will let you inspect the topics and connector created automatically by the Replication Processor.

## Generate some fake data in the source topic

```
$ ./mvnw -f integration-tests/replication-processor/pom.xml test
```

## Accessing the MS SQL sink database to valide the rows have been replicated successfully

```
$ docker exec -it replication-sqlserver bash -c '/opt/mssql-tools/bin/sqlcmd -U sa -P Password!'

1> use tests;
2> go
1> SELECT count(1) from PricingPublish;
2> go
```