> [Back to Main Menu](../README.md)

# Configuring Change Data Capture for MySQL

## Example configuration

```
curl -X POST http://localhost:8080/replication-definition \
  -H "Content-Type: application/json"  \
  --data-binary @- << EOF
{
  "name":"mysql-replication",
  "sourceTopic":{
    "name":"cdc-pricing-publish",
    "compacted":false,
    "partitions":1,
    "replicationFactor":1
  },
  "source":{
    "type":"MYSQL",
    "configs": {
      "database.hostname": "mysql",
      "database.server.id": "1100"
      "database.port": "3306",
      "database.user": "mysql-cdc",
      "database.password": "mysql-cdc",
      "database.whitelist": "tests"
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
      "type":"MYSQL",
      "tasksMax":1,
      "name":"mysql-test",
      "configs":{
        "connection.url":"jdbc:mysql://mysql:3306/tests?useUnicode=yes&characterEncoding=UTF-8",
        "connection.user":"mysql-user",
        "connection.password":"mysql-password",
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