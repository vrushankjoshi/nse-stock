package com.dao;

import java.io.Serializable;
import java.util.Date;

public class StockPrimaryKey implements Serializable {

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

    public void setChTimestamp(Date chTimestamp) {
        this.chTimestamp = chTimestamp;
    }
}
