package example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.model.StockTransaction;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class StockTransactionDeserializer implements Deserializer<StockTransaction> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public StockTransaction deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, StockTransaction.class);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize StockTransaction", e);
        }
    }
}
