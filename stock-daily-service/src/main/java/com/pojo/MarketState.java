package com.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketState {

    @JsonProperty(value = "market")
    private String market;

    @JsonProperty(value = "marketStatus")
    private String marketStatus;

    @JsonProperty(value = "tradeDate")
    private String tradeDate;

    @JsonProperty(value = "index")
    private String index;

    @JsonProperty(value = "last")
    private String last;

    @JsonProperty(value = "variation")
    private String variation;

    @JsonProperty(value = "percentChange")
    private String percentChange;

    @JsonProperty(value = "marketStatusMessage")
    private String marketStatusMessage;

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(String marketStatus) {
        this.marketStatus = marketStatus;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public String getMarketStatusMessage() {
        return marketStatusMessage;
    }

    public void setMarketStatusMessage(String marketStatusMessage) {
        this.marketStatusMessage = marketStatusMessage;
    }
}
