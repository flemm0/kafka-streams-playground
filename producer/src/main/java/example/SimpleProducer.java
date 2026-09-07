package example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.time.Instant;

public class SimpleProducer {

    private static final Logger logger = LogManager.getLogger(SimpleProducer.class);
    private static final String TOPIC = "my-topic";
    private static final double MESSAGES_PER_SECOND = 2.0;

    public static void main(String[] args) {
        // Set up producer properties
        logger.info("Setting up producer properties...");

        Properties properties = new Properties();
        
        // Required properties
        properties.put("bootstrap.servers", "localhost:19092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        // Optional properties
        properties.put("acks", "all"); // Ensure all replicas acknowledge the message
        properties.put("retries", 3); // Retry sending the message up to 3 times
        properties.put("compression.type", "snappy"); // Use Snappy compression for better performance

        logger.info("Producer properties set up.");

        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            logger.info("Producer started. Sending messages to topic: {}", TOPIC);
            while (!Thread.currentThread().isInterrupted()) {
                String key = "key-" + (int) (Math.random() * 100_000_000);
                String value = "value-" + (int) (Math.random() * 100_000_000) + " at " + Instant.now().toString();
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, value);
                producer.send(record);

                // Exponential inter-arrival time => Poisson-distributed event count
                double delayMs = -Math.log(1.0 - Math.random()) / MESSAGES_PER_SECOND * 1000.0;
                Thread.sleep((long) delayMs);
            }
        } catch (InterruptedException e) {
            logger.info("Producer interrupted, shutting down...");
            Thread.currentThread().interrupt();
        }
    }
}
