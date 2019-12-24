#!/bin/bash

confluent-hub install --component-dir ./connectors  debezium/debezium-connector-sqlserver:0.10.0 && \
  cp connectors/debezium-debezium-connector-sqlserver/lib/mssql-jdbc-7.2.2.jre8.jar connect-libs/mssql-jdbc-7.2.2.jre8.jar && \
  confluent-hub install --component-dir ./connectors  debezium/debezium-connector-mysql:0.9.4 && \
  cp connectors/debezium-debezium-connector-mysql/lib/mysql-connector-java-8.0.13.jar connect-libs/mysql-connector-java-8.0.13.jar