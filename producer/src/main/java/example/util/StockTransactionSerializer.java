package example.util;

import example.model.StockTransaction;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.errors.SerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StockTransactionSerializer implements Serializer<StockTransaction> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, StockTransaction data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing StockTransaction", e);
        }
    }
}
