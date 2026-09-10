.PHONY: help start-kafka stop-kafka create-all-topics run-simple-producer run-stock-transaction-producer

help: ## Show available commands
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "%-20s %s\n", $$1, $$2}'

start-kafka: ## Start Kafka containers
	docker-compose up -d

stop-kafka: ## Stop Kafka containers
	docker-compose down

create-all-topics: ## Create all topics
	bash scripts/create-topics.sh

run-simple-producer: ## Run the Kafka SimpleProducer
	mvn -pl producer exec:java -Dexec.mainClass=example.SimpleProducer

run-stock-transaction-producer: ## Run the Kafka StockTransactionProducer
	mvn -pl producer exec:java -Dexec.mainClass=example.StockTransactionProducer
