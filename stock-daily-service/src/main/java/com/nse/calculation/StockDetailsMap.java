package com.nse.calculation;

import com.pojo.Stock;

import java.util.TreeMap;

public class StockDetailsMap extends TreeMap {

    public StockDetailsMap() {
        super();
    }

    public Stock put(long key, Stock value) {
        super.put(key, value);
        return value;
    }

}
