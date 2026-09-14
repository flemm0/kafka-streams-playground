package example;

import example.model.StockTransaction;
import example.model.TransactionSummary;
import example.util.StockTransactionDeserializer;
import example.util.StockTransactionSerializer;
import example.util.TransactionSummaryDeserializer;
import example.util.TransactionSummarySerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.time.Duration;

public class TickerWindowAggregator {

    private static final Logger LOG = LogManager.getLogger(TickerWindowAggregator.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        LOG.info("Starting TickerWindowAggregator...");
        LOG.info("Setting up Kafka Streams configuration...");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ticker-window-aggregator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");

        StreamsConfig streamsConfig = new StreamsConfig(props);

        Serde<StockTransaction> transactionSerde = Serdes.serdeFrom(
            new StockTransactionSerializer(),
            new StockTransactionDeserializer()
        );
        Serde<TransactionSummary> summarySerde = Serdes.serdeFrom(
            new TransactionSummarySerializer(),
            new TransactionSummaryDeserializer()
        );

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, StockTransaction> transactionsStream = 
            builder.stream("market.trades.raw", Consumed.with(Serdes.String(), transactionSerde));
        KStream<String, TransactionSummary> summaryStream = transactionsStream
            .selectKey((key, trade) -> trade.getTicker())
            .groupByKey(Grouped.with(Serdes.String(), transactionSerde))
            .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(10)))
            .aggregate(
                TransactionSummary::new,
                (ticker, trade, summary) -> {
                    summary.setTicker(ticker);
                    summary.setTradeCount(summary.getTradeCount() + 1);
                    summary.setTotalQuantity(summary.getTotalQuantity() + trade.getQuantity());
                    summary.setTotalNotional(summary.getTotalNotional() + (trade.getPrice() * trade.getQuantity()));
                    summary.setVwap(summary.getTotalNotional() / summary.getTotalQuantity());
                    summary.setBuyQuantity(summary.getBuyQuantity() + (trade.getTradeType().equals("BUY") ? trade.getQuantity() : 0));
                    summary.setSellQuantity(summary.getSellQuantity() + (trade.getTradeType().equals("SELL") ? trade.getQuantity() : 0));
                    return summary;
                },
                Materialized.<String, TransactionSummary, WindowStore<Bytes, byte[]>>as("ticker-window-summary-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(summarySerde)
            )
            .toStream()
            .map((windowedKey, summary) -> {
                summary.setWindowStart(windowedKey.window().start());
                return KeyValue.pair(windowedKey.key(), summary);
            });
        summaryStream.peek((key, summary) -> LOG.info("Aggregated summary for ticker {}: {}", key, summary))
            .to("market.trades.summary", Produced.with(Serdes.String(), summarySerde));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);

        kafkaStreams.setUncaughtExceptionHandler(exception -> {
            LOG.error("Unhandled exception in Kafka Streams, shutting down", exception);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Shutdown signal received, closing KafkaStreams...");
            kafkaStreams.close(Duration.ofSeconds(10));
        }, "streams-shutdown-hook"));

        kafkaStreams.start();
    }

}
