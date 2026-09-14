package example.model;

/** Aggregate of all trades for a ticker in a single time window. */
public class TransactionSummary {

    private long windowStart;
    private String ticker;
    private long tradeCount;
    private double totalQuantity;
    private double totalNotional;
    private double vwap;
    private double buyQuantity;
    private double sellQuantity;

    public TransactionSummary() {
    }

    public TransactionSummary(long windowStart, String ticker, long tradeCount, double totalQuantity, double totalNotional, double vwap, double buyQuantity, double sellQuantity) {
        this.windowStart = windowStart;
        this.ticker = ticker;
        this.tradeCount = tradeCount;
        this.totalQuantity = totalQuantity;
        this.totalNotional = totalNotional;
        this.vwap = vwap;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
    }

    public long getWindowStart() { return windowStart; }

    public void setWindowStart(long windowStart) { this.windowStart = windowStart; }

    public String getTicker() { return ticker; }

    public void setTicker(String ticker) { this.ticker = ticker; }

    public long getTradeCount() { return tradeCount; }

    public void setTradeCount(long tradeCount) { this.tradeCount = tradeCount; }

    public double getTotalQuantity() { return totalQuantity; }

    public void setTotalQuantity(double totalQuantity) { this.totalQuantity = totalQuantity; }

    public double getTotalNotional() { return totalNotional; }

    public void setTotalNotional(double totalNotional) { this.totalNotional = totalNotional; }

    public double getVwap() { return vwap; }

    public void setVwap(double vwap) { this.vwap = vwap; }

    public double getBuyQuantity() { return buyQuantity; }

    public void setBuyQuantity(double buyQuantity) { this.buyQuantity = buyQuantity; }

    public double getSellQuantity() { return sellQuantity; }

    public void setSellQuantity(double sellQuantity) { this.sellQuantity = sellQuantity; }
}
