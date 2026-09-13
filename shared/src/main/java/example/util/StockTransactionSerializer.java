package example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.model.StockTransaction;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class StockTransactionSerializer implements Serializer<StockTransaction> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, StockTransaction data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing StockTransaction", e);
        }
    }
}
