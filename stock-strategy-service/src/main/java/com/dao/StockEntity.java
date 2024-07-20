package com.dao;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document("daily_stock")
public class StockEntity implements Stock {


    @Id
    ObjectId objId;
    /*@Id
    private StockPrimaryKey stockPrimaryKey;*/

    private String chSymbol = null;

    private Date chTimestamp = null;

    public String getChSymbol() {
        return chSymbol;
    }

    public void setChSymbol(String chSymbol) {
        this.chSymbol = chSymbol;
    }

    public Date getChTimestamp() {
        return chTimestamp;
    }

    public ObjectId getObjId() {
        return objId;
    }

    public void setObjId(ObjectId objId) {
        this.objId = objId;
    }

    public void setChTimestamp(Date chTimestamp) {
        this.chTimestamp = chTimestamp;
    }

    private Date weekStartDate = null;


    private Date weekEndDate = null;

    private float sma;

    private float ema;


    private double rsi;

    private float chClosingPrice = 0;

    private float ch52WeekHighPrice = 0;

    private float ch52WeekLowPrice = 0;

   /* public StockPrimaryKey getStockPrimaryKey() {
        return stockPrimaryKey;
    }

    public void setStockPrimaryKey(StockPrimaryKey stockPrimaryKey) {
        this.stockPrimaryKey = stockPrimaryKey;
    }*/

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

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof StockEntity))
            return false;

        StockEntity entity = (StockEntity) o;
        if (this.getChSymbol().equals(entity.getChSymbol()) &&
                this.getChTimestamp().equals(entity.getChTimestamp()))
            return true;

        return false;

    }

    @Override
    public final int hashCode() {
        return (int) (getChTimestamp().getTime() + getChSymbol().hashCode());
    }
}
