package example.model;

public class StockTransaction {

    private String tradeId;
    private String ticker;
    private double price;
    private double quantity;
    private long tradeTimestamp;
    private String exchange;
    private StockTradeCondition tradeCondition;
    private String tradeType;

    public StockTransaction(String tradeId, String ticker, double price,
                            double quantity, long tradeTimestamp, String exchange,
                            StockTradeCondition tradeCondition, String tradeType) {
        this.tradeId = tradeId;
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
        this.tradeTimestamp = tradeTimestamp;
        this.exchange = exchange;
        this.tradeCondition = tradeCondition;
        this.tradeType = tradeType;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public String getExchange() {
        return exchange;
    }

    public StockTradeCondition getTradeCondition() {
        return tradeCondition;
    }

    public String getTradeType() {
        return tradeType;
    }

}
