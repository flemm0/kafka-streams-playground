package example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.model.TransactionSummary;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class TransactionSummaryDeserializer implements Deserializer<TransactionSummary> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TransactionSummary deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, TransactionSummary.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing TransactionSummary", e);
        }
    }
}
