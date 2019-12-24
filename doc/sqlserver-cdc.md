> [Back to Main Menu](../README.md)

# Configuring Change Data Capture for SQL Server

## Example configuration

```
curl -X POST http://localhost:8080/replication-definition \
  -H "Content-Type: application/json"  \
  --data-binary @- << EOF
{
  "name":"sqlserver-replication",
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