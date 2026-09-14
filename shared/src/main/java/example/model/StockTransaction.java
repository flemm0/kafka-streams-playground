package example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StockTransaction {

    private String tradeId;
    private String ticker;
    private double price;
    private double quantity;
    private long tradeTimestamp;
    private String exchange;
    private StockTradeCondition tradeCondition;
    private String tradeType;

    @JsonCreator
    public StockTransaction(@JsonProperty("tradeId") String tradeId,
                            @JsonProperty("ticker") String ticker,
                            @JsonProperty("price") double price,
                            @JsonProperty("quantity") double quantity,
                            @JsonProperty("tradeTimestamp") long tradeTimestamp,
                            @JsonProperty("exchange") String exchange,
                            @JsonProperty("tradeCondition") StockTradeCondition tradeCondition,
                            @JsonProperty("tradeType") String tradeType) {
        this.tradeId = tradeId;
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
        this.tradeTimestamp = tradeTimestamp;
        this.exchange = exchange;
        this.tradeCondition = tradeCondition;
        this.tradeType = tradeType;
    }

    public String getTradeId() { return tradeId; }
    public String getTicker() { return ticker; }
    public double getPrice() { return price; }
    public double getQuantity() { return quantity; }
    public long getTradeTimestamp() { return tradeTimestamp; }
    public String getExchange() { return exchange; }
    public StockTradeCondition getTradeCondition() { return tradeCondition; }
    public String getTradeType() { return tradeType; }
}
