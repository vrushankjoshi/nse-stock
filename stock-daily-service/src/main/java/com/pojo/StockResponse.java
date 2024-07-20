package com.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "daily_stock")
public class StockResponse {

    public StockResponse() {

    }

    public StockResponse(String chSymbol, Date chTimestamp, Date weekStartDate, Date weekEndDate,
                         float sma, float ema, double rsi, float chClosingPrice, float ch52WeekHighPrice,
                         float ch52WeekLowPrice) {

        this.chSymbol = chSymbol;
        this.chTimestamp = chTimestamp;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.sma = sma;
        this.ema = ema;
        this.rsi = rsi;
        this.chClosingPrice = chClosingPrice;
        this.ch52WeekHighPrice = ch52WeekHighPrice;
        this.ch52WeekLowPrice = ch52WeekLowPrice;

    }


    @JsonProperty(value = "CH_SYMBOL", index = 1)
    private String chSymbol = null;


    @JsonProperty(value = "CH_TIMESTAMP", index = 2)
    private Date chTimestamp = null;

    @JsonProperty(value = "weekStartDate", index = 3)
    private Date weekStartDate = null;


    @JsonProperty(value = "weekEndDate", index = 4)
    private Date weekEndDate = null;

    @JsonProperty(value = "sma", index = 5)
    private float sma;

    @JsonProperty(value = "ema", index = 6)
    private float ema;


    @JsonProperty(value = "rsi", index = 7)
    private double rsi;

    @JsonProperty(value = "CH_CLOSING_PRICE", index = 8)
    private float chClosingPrice = 0;

    @JsonProperty(value = "CH_52WEEK_HIGH_PRICE", index = 9)
    private float ch52WeekHighPrice = 0;

    @JsonProperty(value = "CH_52WEEK_LOW_PRICE", index = 10)
    private float ch52WeekLowPrice = 0;


    public String getChSymbol() {
        return chSymbol;
    }

    public void setChSymbol(String chSymbol) {
        this.chSymbol = chSymbol;
    }

    public Date getChTimestamp() {
        return chTimestamp;
    }

    public void setChTimestamp(Date chTimestamp) {
        this.chTimestamp = chTimestamp;
    }

    public Date getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(Date weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public Date getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(Date weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public float getSma() {
        return sma;
    }

    public void setSma(float sma) {
        this.sma = sma;
    }

    public float getEma() {
        return ema;
    }

    public void setEma(float ema) {
        this.ema = ema;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public float getChClosingPrice() {
        return chClosingPrice;
    }

    public void setChClosingPrice(float chClosingPrice) {
        this.chClosingPrice = chClosingPrice;
    }

    public float getCh52WeekHighPrice() {
        return ch52WeekHighPrice;
    }

    public void setCh52WeekHighPrice(float ch52WeekHighPrice) {
        this.ch52WeekHighPrice = ch52WeekHighPrice;
    }

    public float getCh52WeekLowPrice() {
        return ch52WeekLowPrice;
    }

    public void setCh52WeekLowPrice(float ch52WeekLowPrice) {
        this.ch52WeekLowPrice = ch52WeekLowPrice;
    }
}
