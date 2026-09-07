.PHONY: help run-simple-producer

help: ## Show available commands
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "%-20s %s\n", $$1, $$2}'

run-simple-producer: ## Run the Kafka SimpleProducer
	mvn -pl producer exec:java -Dexec.mainClass=example.SimpleProducer
