package example.util;

import example.model.StockTransaction;
import example.model.StockTradeCondition;
import net.datafaker.Faker;
import java.util.UUID;

public class DataGenerator {
    
    public static StockTransaction generateRandomStockTransaction() {
        var faker = new Faker();
        String nyseOrNasdaq = faker.random().nextBoolean() ? "NYSE" : "NASDAQ";

        return new StockTransaction(
            UUID.randomUUID().toString(),
            nyseOrNasdaq.equals("NYSE") ? faker.stock().nyseSymbol() : faker.stock().nsdqSymbol(),
            faker.random().nextDouble() * 1000,
            faker.random().nextDouble() * 100,
            System.currentTimeMillis(),
            nyseOrNasdaq,
            faker.random().nextEnum(StockTradeCondition.class),
            faker.random().nextBoolean() ? "BUY" : "SELL"
        );
    }
}
