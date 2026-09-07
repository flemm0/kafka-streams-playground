# Sandbox Stock Trading Platform — Kafka Source Data Model

Scope: only **producer-side source topics** (raw events as they'd be emitted by upstream systems — matching engine, OMS, ledger, exchange feed, reference-data service). No consumers, no derived/aggregated streams (those would be Flink/Spark/ksqlDB output topics, out of scope here).

Design conventions:
- Schema format: Avro-style (works cleanly with Schema Registry / dlt / Databricks Autoloader either way)
- Keys are chosen for correct partitioning + downstream compaction where relevant
- `cleanup.policy` noted per topic: `delete` for immutable event streams, `compact` for latest-state/reference topics
- All timestamps: epoch millis, UTC

---

## 1. `market.trades.raw`
Simulated exchange tick feed — one record per executed trade.

- **Key:** `symbol` (string)
- **Partitions:** 12 (hash on symbol keeps per-symbol ordering)
- **Cleanup policy:** `delete`, retention 7d
- **Volume:** highest-throughput topic in the sandbox

```json
{
  "trade_id": "uuid",
  "symbol": "AAPL",
  "price": 231.47,
  "quantity": 100,
  "trade_timestamp": 1725700000123,
  "exchange": "SANDBOX_NASDAQ",
  "trade_condition": "REGULAR",     // REGULAR | ODD_LOT | AVG_PRICE
  "sequence_number": 8823145
}
```

## 2. `market.quotes.raw`
Top-of-book bid/ask feed (simplified L1; skip full order book depth for the sandbox).

- **Key:** `symbol`
- **Partitions:** 12
- **Cleanup policy:** `delete`, retention 1–2d (quotes are high-volume and low-value historically)

```json
{
  "quote_id": "uuid",
  "symbol": "AAPL",
  "bid_price": 231.45,
  "bid_size": 500,
  "ask_price": 231.49,
  "ask_size": 300,
  "quote_timestamp": 1725700000456,
  "exchange": "SANDBOX_NASDAQ"
}
```

## 3. `orders.lifecycle`
Order state transitions emitted by the OMS/matching engine.

- **Key:** `order_id`
- **Partitions:** 6 (keeps all events for one order in order)
- **Cleanup policy:** `delete`, retention 30d (orders are auditable, so longer retention than market data)

```json
{
  "event_id": "uuid",
  "order_id": "uuid",
  "client_order_id": "string",
  "account_id": "uuid",
  "symbol": "AAPL",
  "side": "BUY",                    // BUY | SELL
  "order_type": "LIMIT",            // MARKET | LIMIT | STOP | STOP_LIMIT
  "limit_price": 231.00,            // nullable — null for MARKET
  "stop_price": null,
  "quantity": 100,
  "time_in_force": "DAY",           // DAY | GTC | IOC | FOK
  "status": "NEW",                  // NEW | PARTIALLY_FILLED | FILLED | CANCELLED | REJECTED | EXPIRED
  "filled_quantity": 0,
  "avg_fill_price": null,
  "reject_reason": null,            // e.g. INSUFFICIENT_FUNDS, INVALID_SYMBOL
  "event_timestamp": 1725700001000
}
```

## 4. `orders.executions`
Individual fill/execution reports — separate from lifecycle since one order can generate many partial fills (classic FIX ExecutionReport pattern).

- **Key:** `order_id`
- **Partitions:** 6
- **Cleanup policy:** `delete`, retention 30d

```json
{
  "execution_id": "uuid",
  "order_id": "uuid",
  "account_id": "uuid",
  "symbol": "AAPL",
  "side": "BUY",
  "executed_quantity": 50,
  "executed_price": 231.02,
  "liquidity_flag": "TAKER",        // MAKER | TAKER
  "commission": 0.35,
  "execution_timestamp": 1725700001250
}
```

## 5. `accounts.profile`
Slowly-changing account/user attributes — this is a **latest-state** topic.

- **Key:** `account_id`
- **Partitions:** 3
- **Cleanup policy:** `compact`

```json
{
  "account_id": "uuid",
  "user_id": "uuid",
  "account_type": "MARGIN",         // CASH | MARGIN | RETIREMENT
  "status": "ACTIVE",               // ACTIVE | SUSPENDED | CLOSED
  "kyc_status": "VERIFIED",         // PENDING | VERIFIED | REJECTED
  "risk_tolerance": "MODERATE",
  "country": "US",
  "created_at": 1700000000000,
  "updated_at": 1725699000000
}
```

## 6. `accounts.ledger`
Append-only cash ledger — every debit/credit against an account (funding, settlement, fees, dividends, interest). This is the source of truth balances get derived from downstream.

- **Key:** `account_id`
- **Partitions:** 6
- **Cleanup policy:** `delete`, retention indefinite/long (financial audit trail — in a real system this would never expire; pick something like 365d+ for the sandbox)

```json
{
  "transaction_id": "uuid",
  "account_id": "uuid",
  "transaction_type": "TRADE_SETTLEMENT",  // DEPOSIT | WITHDRAWAL | TRADE_SETTLEMENT | FEE | DIVIDEND | INTEREST
  "amount": -11551.00,              // signed; negative = debit
  "currency": "USD",
  "related_order_id": "uuid",       // nullable — only for TRADE_SETTLEMENT/FEE
  "balance_after": 48449.00,        // snapshot for convenience, not authoritative
  "transaction_timestamp": 1725700002000
}
```

## 7. `reference.instruments`
Security master — compacted reference/dimension data.

- **Key:** `symbol`
- **Partitions:** 1–3 (small, low-throughput)
- **Cleanup policy:** `compact`

```json
{
  "symbol": "AAPL",
  "isin": "US0378331005",
  "company_name": "Apple Inc.",
  "sector": "Technology",
  "industry": "Consumer Electronics",
  "exchange": "SANDBOX_NASDAQ",
  "currency": "USD",
  "tick_size": 0.01,
  "lot_size": 1,
  "is_active": true,
  "shares_outstanding": 15300000000,
  "ipo_date": "1980-12-12"
}
```

## 8. `reference.corporate_actions`
Splits, dividends, mergers — sparse, event-driven.

- **Key:** `symbol`
- **Partitions:** 1–3
- **Cleanup policy:** `delete`, long retention (or compact-by-action_id if you want latest-state per action)

```json
{
  "action_id": "uuid",
  "symbol": "AAPL",
  "action_type": "DIVIDEND",        // SPLIT | DIVIDEND | MERGER | SPINOFF
  "split_ratio": null,              // e.g. "4:1" for SPLIT
  "dividend_amount": 0.25,          // nullable — only for DIVIDEND
  "announcement_timestamp": 1725000000000,
  "ex_date": "2026-09-10",
  "record_date": "2026-09-11",
  "pay_date": "2026-09-24"
}
```

## 9. `market.news` (optional enrichment feed)
Simulated news/sentiment ticker — useful if you want to test stream-table joins or windowed sentiment features later.

- **Key:** `null` (round-robin) or first symbol in `symbols[]` if you want per-symbol ordering
- **Partitions:** 3
- **Cleanup policy:** `delete`, retention 14d

```json
{
  "news_id": "uuid",
  "headline": "Apple announces new product line",
  "symbols": ["AAPL"],
  "source": "SANDBOX_WIRE",
  "sentiment_score": 0.62,          // nullable, -1.0 to 1.0
  "published_at": 1725699500000,
  "url": "https://example.com/news/123"
}
```

---

## Topic summary

| Topic | Key | Cleanup | Nature |
|---|---|---|---|
| `market.trades.raw` | symbol | delete | High-volume tick stream |
| `market.quotes.raw` | symbol | delete | High-volume tick stream |
| `orders.lifecycle` | order_id | delete | Order state machine events |
| `orders.executions` | order_id | delete | Fill/execution reports |
| `accounts.profile` | account_id | **compact** | Latest-state dimension |
| `accounts.ledger` | account_id | delete | Append-only financial ledger |
| `reference.instruments` | symbol | **compact** | Security master dimension |
| `reference.corporate_actions` | symbol | delete | Sparse corporate events |
| `market.news` | symbol or null | delete | Optional enrichment feed |

## Notes on realism vs. sandbox simplicity
- A real exchange feed would separate L1/L2/L3 order book depth; this model collapses to L1 quotes only, which is enough to build meaningful stream-processing exercises (spread calculation, VWAP, etc.) without the complexity of book-building.
- `orders.lifecycle` and `orders.executions` are split the way real FIX-based OMSes do it (NewOrderSingle/OrderCancelRequest vs. ExecutionReport) — good practice if you ever want this sandbox to mirror real brokerage architecture.
- Position/portfolio balances are *deliberately excluded* as a producer topic — those are naturally a **derived** stream (fold `orders.executions` + `accounts.ledger` over time), which is exactly the kind of thing your Kafka Streams/Flink app downstream would compute. Keeping it out of the source model keeps producers honest about what's truly raw vs. computed.
