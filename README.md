# kafka-streams-playground

A local sandbox for learning Kafka and Kafka Streams: a 3-broker KRaft cluster in Docker, a synthetic stock-trade producer, and a Streams app that windows and aggregates those trades.

## Modules

| Module | What it is |
| --- | --- |
| `shared` | Domain models (`StockTransaction`, `TransactionSummary`) and their JSON serializers/deserializers |
| `producer` | `SimpleProducer` (hello-world) and `StockTransactionProducer` (fake trades → `market.trades.raw`) |
| `streaming-apps` | `TickerWindowAggregator`: 1-minute tumbling windows per ticker → VWAP, volume, buy/sell split → `market.trades.summary` |

## Requirements

Docker, plus the toolchain pinned in [.sdkmanrc](.sdkmanrc) (Java 23, Maven 3.9) — `sdk env install` if you use SDKMAN.

## Quick start

```bash
make start-kafka             # 3 brokers + kafbat kafka-ui on :8080
make create-all-topics       # my-topic, market.trades.raw, market.trades.summary
make install-shared-module   # shared/ into the local Maven repo

make run-stock-transaction-producer      # in one terminal
make run-stock-transaction-streaming-app # in another
```

Brokers are reachable from the host on `localhost:19092`, `:29092`, `:39092`. Inspect topics at http://localhost:8080. `make stop-kafka` tears everything down — broker data lives in tmpfs, so it does not survive a restart.

`make help` lists every target.

## Docs

- [docs/source-data-model.md](docs/source-data-model.md) — the sandbox trading-platform topic/schema design this is modeled on
- [docs/notes.md](docs/notes.md) — Metals/Bloop fixes and other local gotchas
