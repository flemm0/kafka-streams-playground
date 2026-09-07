package example.model;

class StockTransaction {

    private String tradeId;
    private String ticker;
    private double price;
    private double quantity;
    private long tradeTimestamp;
    private String exchange;
    private String tradeCondition; // REGULAR || ODD_LOT || AVG_PRICE
    private String tradeType; // BUY || SELL

    public StockTransaction(String tradeId, String ticker, double price,
                            double quantity, long tradeTimestamp, String exchange,
                            String tradeCondition, String tradeType) {
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

    public String getTradeCondition() {
        return tradeCondition;
    }

    public String getTradeType() {
        return tradeType;
    }

}
