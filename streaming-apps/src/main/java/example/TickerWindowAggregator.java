package example;

import example.model.StockTransaction;
import example.util.StockTransactionDeserializer;
import example.util.StockTransactionSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class TickerWindowAggregator {

    private static final Logger LOG = LogManager.getLogger(TickerWindowAggregator.class);

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
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, StockTransaction> transactionsStream = 
            builder.stream("market.trades.raw", Consumed.with(Serdes.String(), transactionSerde));
    }

}
