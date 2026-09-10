package example;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;
import java.util.stream.Stream;

import example.model.StockTransaction;
import example.util.DataGenerator;
import example.util.StockTransactionSerializer;

public class StockTransactionProducer {
    
    private static final Logger LOG = LogManager.getLogger(StockTransactionProducer.class);
    private static final String TOPIC = "market.trades.raw";
    private static final double MESSAGES_PER_SECOND = 2.0;

    public static void main(String[] args) {
        // Set up producer properties
        LOG.info("Setting up producer properties...");

        Properties properties = new Properties();
        
        properties.put("bootstrap.servers", "localhost:19092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "all"); // Ensure all replicas acknowledge the message
        properties.put("retries", 3); // Retry sending the message up to 3 times
        properties.put("compression.type", "snappy"); // Use Snappy compression for better performance

        LOG.info("Producer properties set up.");

        try (KafkaProducer<String, StockTransaction> producer = 
                new KafkaProducer<String, StockTransaction>(properties, new StringSerializer(), new StockTransactionSerializer())) {
            LOG.info("starting producing messages...");
            while(!Thread.currentThread().isInterrupted()) {
                int numCurrentTransactions = 1 + (int) Math.round(Math.random() * 10); // send between 1 and 10 transactions per iteration
                Stream<StockTransaction> transactionStream = 
                    DataGenerator.generateRandomStockTransactions(numCurrentTransactions);
                transactionStream.forEach(transaction -> {
                    ProducerRecord<String, StockTransaction> record = new ProducerRecord<>(TOPIC, null, transaction);
                    producer.send(record, (metadata, exception) -> {
                        if (exception != null) {
                            LOG.warn("Error sending {}", transaction, exception);
                        }
                    });
                });

                double delayMs = -Math.log(1.0 - Math.random()) / MESSAGES_PER_SECOND * 1000.0;
                Thread.sleep((long) delayMs);
            }

        } catch (InterruptedException e) {
            LOG.info("Producer interrupted, shutting down...");
            Thread.currentThread().interrupt();
        }
    }

}
