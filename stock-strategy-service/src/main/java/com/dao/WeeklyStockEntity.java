package com.dao;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document("weekly_stock")
public class WeeklyStockEntity {


        @Id
        ObjectId objId;


        private String chSymbol = null;


        public String getChSymbol() {
            return chSymbol;
        }

        public void setChSymbol(String chSymbol) {
            this.chSymbol = chSymbol;
        }



        public ObjectId getObjId() {
            return objId;
        }

        public void setObjId(ObjectId objId) {
            this.objId = objId;
        }


        private Date weekStartDate = null;


        private Date weekEndDate = null;

        private float sma;

        private float ema;


        private double rsi;

        private float chClosingPrice = 0;

        private float ch52WeekHighPrice = 0;

        private float ch52WeekLowPrice = 0;


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
            if (!(o instanceof com.dao.StockEntity))
                return false;

            com.dao.StockEntity entity = (com.dao.StockEntity) o;
            if (this.getChSymbol().equals(entity.getChSymbol()) &&
                    this.getWeekStartDate().equals(entity.getWeekStartDate()))
                return true;

            return false;

        }

        @Override
        public final int hashCode() {
            return (int) (getWeekStartDate().getTime() + getChSymbol().hashCode());
        }
}
