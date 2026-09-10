#!/bin/bash

set -euo pipefail

KAFKA_CONTAINERS=("kafka-1" "kafka-2" "kafka-3")
ALL_RUNNING=true

for container in "${KAFKA_CONTAINERS[@]}"; do
    if [ -z "$(docker ps -q -f name=${container})" ]; then
        echo "Container ${container} is not running."
        ALL_RUNNING=false
    fi
done

if [ "$ALL_RUNNING" = false ]; then
    echo "One or more Kafka containers are not running. Please start the containers before creating topics."
    exit 1
else
    echo "All Kafka containers are running. Proceeding to create topics."

    topics=("my-topic" "market.trades.raw")
    for topic in "${topics[@]}"; do
        echo "Creating topic: ${topic}"
        docker exec -it kafka-1 kafka-topics --create --topic "${topic}" --bootstrap-server kafka-1:9092 --replication-factor 3 --partitions 3
    done
fi
