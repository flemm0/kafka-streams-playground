package example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.model.TransactionSummary;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class TransactionSummarySerializer implements Serializer<TransactionSummary> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, TransactionSummary data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing TransactionSummary", e);
        }
    }
}
