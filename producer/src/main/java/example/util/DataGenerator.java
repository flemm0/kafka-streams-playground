package example.util;

import example.model.StockTransaction;
import example.model.StockTradeCondition;
import net.datafaker.Faker;
import java.util.UUID;
import java.util.stream.Stream;

public class DataGenerator {

    private static final Faker FAKER = new Faker();
    
    public static StockTransaction generateRandomStockTransaction() {
        String nyseOrNasdaq = FAKER.random().nextBoolean() ? "NYSE" : "NASDAQ";

        return new StockTransaction(
            UUID.randomUUID().toString(),
            nyseOrNasdaq.equals("NYSE") ? FAKER.stock().nyseSymbol() : FAKER.stock().nsdqSymbol(),
            FAKER.random().nextDouble() * 1000,
            FAKER.random().nextDouble() * 100,
            System.currentTimeMillis(),
            nyseOrNasdaq,
            FAKER.random().nextEnum(StockTradeCondition.class),
            FAKER.random().nextBoolean() ? "BUY" : "SELL"
        );
    }

    public static Stream<StockTransaction> generateRandomStockTransactions(int nTransactions) {
        if (nTransactions <= 0) {
            throw new IllegalArgumentException("Number of transactions must be positive.");
        }
        return FAKER.<StockTransaction>stream(
            DataGenerator::generateRandomStockTransaction)
        .len(nTransactions)
        .generate();
    }
}
