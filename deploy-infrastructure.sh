#!/bin/bash

./destroy-infrastructure.sh && \
  docker-compose up -d && \
  ./scripts/create-mysql-cdc-account.sh