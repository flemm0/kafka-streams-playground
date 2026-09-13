package example.model;

/** Aggregate of all trades for a ticker in a single time window. */
public record TransactionSummary(
        long windowStart,
        String ticker,
        long tradeCount,
        double totalQuantity,
        double totalNotional,
        double vwap,
        double buyQuantity,
        double sellQuantity) {
}
