package com.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock implements Cloneable, Comparable<Stock> {

    //"CH_TOTAL_TRADES": 38662,


    @JsonProperty(value = "CH_TOTAL_TRADES")
    private float chTotalTrades = 0;

    @JsonProperty("CH_OPENING_PRICE")
    private float chOpeningPrice = 0;

    @JsonProperty("CH_TOT_TRADED_VAL")
    private float chTotTradedVal = 0;

    @JsonProperty("CH_TRADE_LOW_PRICE")
    private float chTradeLowPrice = 0;

    @JsonProperty(value = "CH_SYMBOL", index = 1)
    private String chSymbol = null;


    @JsonProperty("CH_PREVIOUS_CLS_PRICE")
    private float chPreviousClsPrice = 0;


    @JsonProperty("CH_52WEEK_HIGH_PRICE")
    private float ch52WeekHighPrice = 0;


    @JsonProperty("CH_TIMESTAMP")
    private Date chTimestamp = null;


    @JsonProperty("CH_MARKET_TYPE")
    private char chMarketType;


    @JsonProperty("mTIMESTAMP")
    private String mTimestamp = null;


    @JsonProperty("CH_TOT_TRADED_QTY")
    private float chTotTradedQty = 0;


    @JsonProperty("createdAt")
    private Timestamp createdAt = null;


    @JsonProperty("CH_LAST_TRADED_PRICE")
    private float chLastTradedPrice = 0;


    @JsonProperty("CH_ISIN")
    private String chIsin = null;

    @JsonProperty("CH_SERIES")
    private String chSeries = null;

    @JsonProperty("CH_TRADE_HIGH_PRICE")
    private float chTradeHighPrice = 0;

    @JsonProperty("CH_52WEEK_LOW_PRICE")
    private float ch52WeekLowPrice = 0;


    @JsonProperty("TIMESTAMP")
    private Timestamp timestamp = null;


    @JsonProperty("__v")
    private int __v = 0;


    @JsonProperty("VWAP")
    private float vwap = 0;


    @JsonProperty("CH_CLOSING_PRICE")
    private float chClosingPrice = 0;


    @JsonProperty("_id")
    private String id = null;


    @JsonProperty("updatedAt")
    private Timestamp updatedAt = null;

    @JsonProperty("rsi")
    private double rsi;


    private float priceDifference;

    @JsonProperty("weekStartDate")
    private Date weekStartDate = null;


    @JsonProperty("weekEndDate")
    private Date weekEndDate = null;

    @JsonProperty("sma")
    private float sma;

    @JsonProperty("ema")
    private float ema;

    @JsonIgnore
    private float avgPositivePerRsi;

    @JsonIgnore
    private float avgNegativePerRsi;


    public Stock() {

    }

    public Stock(float chTotalTrades, float chOpeningPrice, float chTotTradedVal, float chTradeLowPrice, String chSymbol, float chPreviousClsPrice, float ch52WeekHighPrice, Date chTimestamp, char chMarketType, String mTimestamp, float chTotTradedQty, Timestamp createdAt, float chLastTradedPrice, String chIsin, String chSeries, float chTradeHighPrice, float ch52WeekLowPrice, Timestamp timestamp, int __v, float vwap, float chClosingPrice, String id, Timestamp updatedAt) {
        this.chTotalTrades = chTotalTrades;
        this.chOpeningPrice = chOpeningPrice;
        this.chTotTradedVal = chTotTradedVal;
        this.chTradeLowPrice = chTradeLowPrice;
        this.chSymbol = chSymbol;
        this.chPreviousClsPrice = chPreviousClsPrice;
        this.ch52WeekHighPrice = ch52WeekHighPrice;
        this.chTimestamp = chTimestamp;
        this.chMarketType = chMarketType;
        this.mTimestamp = mTimestamp;
        this.chTotTradedQty = chTotTradedQty;
        this.createdAt = createdAt;
        this.chLastTradedPrice = chLastTradedPrice;
        this.chIsin = chIsin;
        this.chSeries = chSeries;
        this.chTradeHighPrice = chTradeHighPrice;
        this.ch52WeekLowPrice = ch52WeekLowPrice;
        this.timestamp = timestamp;
        this.__v = __v;
        this.vwap = vwap;
        this.chClosingPrice = chClosingPrice;
        this.id = id;
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public float getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(float priceDifference) {
        this.priceDifference = priceDifference;
    }

    @JsonProperty(index = 10)
    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    @JsonIgnore
    public float getChTotalTrades() {
        return chTotalTrades;
    }

    public void setChTotalTrades(float chTotalTrades) {
        this.chTotalTrades = chTotalTrades;
    }

    @JsonIgnore
    public float getChOpeningPrice() {
        return chOpeningPrice;
    }

    public void setChOpeningPrice(float chOpeningPrice) {
        this.chOpeningPrice = chOpeningPrice;
    }

    @JsonIgnore
    public float getChTotTradedVal() {
        return chTotTradedVal;
    }

    public void setChTotTradedVal(float chTotTradedVal) {
        this.chTotTradedVal = chTotTradedVal;
    }

    @JsonIgnore
    public float getChTradeLowPrice() {
        return chTradeLowPrice;
    }

    public void setChTradeLowPrice(float chTradeLowPrice) {
        this.chTradeLowPrice = chTradeLowPrice;
    }

    @JsonProperty(index = 1)
    public String getChSymbol() {
        return chSymbol;
    }

    public void setChSymbol(String chSymbol) {
        this.chSymbol = chSymbol;
    }

    @JsonIgnore
    public float getChPreviousClsPrice() {
        return chPreviousClsPrice;
    }


    public void setChPreviousClsPrice(float chPreviousClsPrice) {
        this.chPreviousClsPrice = chPreviousClsPrice;
    }

    @JsonProperty(index = 12)
    public float getCh52WeekHighPrice() {
        return ch52WeekHighPrice;
    }

    public void setCh52WeekHighPrice(float ch52WeekHighPrice) {
        this.ch52WeekHighPrice = ch52WeekHighPrice;
    }

    @JsonProperty(index = 2)
    public Date getChTimestamp() {
        return chTimestamp;
    }

    public void setChTimestamp(Date chTimestamp) {
        this.chTimestamp = chTimestamp;
    }

    @JsonIgnore
    public char getChMarketType() {
        return chMarketType;
    }

    public void setChMarketType(char chMarketType) {
        this.chMarketType = chMarketType;
    }

    @JsonIgnore
    public String getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    @JsonIgnore
    public float getChTotTradedQty() {
        return chTotTradedQty;
    }

    public void setChTotTradedQty(float chTotTradedQty) {
        this.chTotTradedQty = chTotTradedQty;
    }

    @JsonIgnore
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public float getChLastTradedPrice() {
        return chLastTradedPrice;
    }

    public void setChLastTradedPrice(float chLastTradedPrice) {
        this.chLastTradedPrice = chLastTradedPrice;
    }

    @JsonIgnore
    public String getChIsin() {
        return chIsin;
    }

    public void setChIsin(String chIsin) {
        this.chIsin = chIsin;
    }

    @JsonIgnore
    public String getChSeries() {
        return chSeries;
    }

    public void setChSeries(String chSeries) {
        this.chSeries = chSeries;
    }

    @JsonIgnore
    public float getChTradeHighPrice() {
        return chTradeHighPrice;
    }

    public void setChTradeHighPrice(float chTradeHighPrice) {
        this.chTradeHighPrice = chTradeHighPrice;
    }

    @JsonProperty(index = 11)
    public float getCh52WeekLowPrice() {
        return ch52WeekLowPrice;
    }

    public void setCh52WeekLowPrice(float ch52WeekLowPrice) {
        this.ch52WeekLowPrice = ch52WeekLowPrice;
    }

    @JsonIgnore
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @JsonIgnore
    public int get__v() {
        return __v;
    }


    public void set__v(int __v) {
        this.__v = __v;
    }

    @JsonIgnore
    public float getVwap() {
        return vwap;
    }

    public void setVwap(float vwap) {
        this.vwap = vwap;
    }

    @JsonProperty(index = 3)
    public float getChClosingPrice() {
        return chClosingPrice;
    }

    public void setChClosingPrice(float chClosingPrice) {
        this.chClosingPrice = chClosingPrice;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty(index = 5)
    public Date getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(Date weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    @JsonProperty(index = 7)
    public Date getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(Date weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    @JsonProperty(index = 8)
    public float getSma() {
        return sma;
    }

    public void setSma(float sma) {
        this.sma = sma;
    }

    @JsonProperty(index = 9)
    public float getEma() {
        return ema;
    }

    public void setEma(float ema) {
        this.ema = ema;
    }

    @JsonIgnore
    public float getAvgPositivePerRsi() {
        return avgPositivePerRsi;
    }

    public void setAvgPositivePerRsi(float avgPositivePerRsi) {
        this.avgPositivePerRsi = avgPositivePerRsi;
    }

    @JsonIgnore
    public float getAvgNegativePerRsi() {
        return avgNegativePerRsi;
    }

    public void setAvgNegativePerRsi(float avgNegativePerRsi) {
        this.avgNegativePerRsi = avgNegativePerRsi;
    }

    public String toString() {
        return this.getChTimestamp() + "," + this.getChClosingPrice() + "," + this.getRsi() + "," + this.getSma() + "," + this.getEma();
    }

    @Override
    public Stock clone() {
        Stock user = null;
        try {
            user = (Stock) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            user = new Stock();

            user.setChTimestamp(this.getChTimestamp());
            user.setChClosingPrice(this.getChClosingPrice());
            user.setRsi(this.getRsi());
            user.setSma(this.getSma());
            user.setEma(this.getEma());
        }
        return user;
    }

    @Override
    public int compareTo(Stock otherStock) {
        return getChTimestamp().compareTo(otherStock.getChTimestamp());
    }

    @Override
    public boolean equals(Object obj) {
        return ((Stock) obj).getChTimestamp().equals(getChTimestamp());
    }


    /*
    "CH_TOTAL_TRADES": 38662,
            "CH_OPENING_PRICE": 1405,
            "CH_TOT_TRADED_VAL": 1393205096.15,
            "CH_TRADE_LOW_PRICE": 1376,
            "CH_SYMBOL": "TECHM",
            "CH_PREVIOUS_CLS_PRICE": 1389.9,
            "CH_52WEEK_HIGH_PRICE": 1406.2,
            "CH_TIMESTAMP": "2024-01-20",
            "CH_MARKET_TYPE": "N",
            "mTIMESTAMP": "20-Jan-2024",
            "CH_TOT_TRADED_QTY": 1001018,
            "createdAt": "2024-01-20T12:21:03.069Z",
            "CH_LAST_TRADED_PRICE": 1385,
            "CH_ISIN": "INE669C01036",
            "CH_SERIES": "EQ",
            "CH_TRADE_HIGH_PRICE": 1406.2,
            "CH_52WEEK_LOW_PRICE": 981.05,
            "TIMESTAMP": "2024-01-19T18:30:00.000Z",
            "__v": 0,
            "VWAP": 1391.79,
            "CH_CLOSING_PRICE": 1385.6,
            "_id": "65abbaaf54ac362a3a37925e",
            "updatedAt": "2024-01-20T12:21:03.069Z"*/
}
