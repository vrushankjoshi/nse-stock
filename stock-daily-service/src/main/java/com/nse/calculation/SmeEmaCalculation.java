package com.nse.calculation;

import com.pojo.Stock;

import java.text.DecimalFormat;
import java.util.*;

public class SmeEmaCalculation {

    public Stock[] calculateSmaEma(Stock[] response, int rsiLength) {
        float devider = rsiLength + 1;
        float multiplier = 2 / devider;
        DecimalFormat df = new DecimalFormat("#.##");

        //System.out.println("[calculateSmaEma] - multiplier ->" + multiplier +"<--, total objects -->" + response.length);
        Map calculatedMap = new HashMap();
        Stock stock = null;
        NavigableMap myMap = new StockDetailsMap();

        for (Stock stockDetail : response) {
            //System.out.println(stockDetail.getChTimestamp()+", "+stockDetail.getChClosingPrice()+", "+stockDetail.getChPreviousClsPrice());
            myMap.put(stockDetail.getChTimestamp().getTime(), stockDetail);

        }

        //logger.info("[calculateSmaEma] - total myMap -->" + myMap.size());

        Object[] keys = (new TreeSet(myMap.keySet())).toArray();
        //logger.info("[calculateSmaEma] - total keys -->" + keys.length);
        for (int i = 0; i < rsiLength; i++) {
            stock = (Stock) myMap.get((Long) keys[i]);
            calculatedMap.put(stock.getChTimestamp(), stock);
        }

        float sumVal = 0;

        float tempVal = 0.00F;
        Stock tempStock = null;
        for (int i = rsiLength - 1; i < keys.length; i++) {
            sumVal = 0;
            stock = (Stock) myMap.get((Long) keys[i]);
            for (int j = i; j > i - rsiLength; j--) {
                tempStock = (Stock) myMap.get(keys[j]);
                sumVal += tempStock.getChClosingPrice();
            }
            tempVal = sumVal / (float) rsiLength;
            stock.setSma(Float.parseFloat(df.format(tempVal)));
            if (i == (rsiLength - 1)) {
                stock.setEma(Float.parseFloat(df.format(tempVal)));
            } else {
                tempStock = (Stock) myMap.get(keys[i - 1]);
                sumVal = (stock.getChClosingPrice() * multiplier) + (tempStock.getEma() * (1 - multiplier));
                stock.setEma(Float.parseFloat(df.format(sumVal)));

            }
            //System.out.println(" date " + stock.getChTimestamp() + ", price ->"+ stock.getChClosingPrice() +" , sma ->" + stock.getSma() + " , ema ->" + stock.getEma());
            calculatedMap.put(stock.getChTimestamp(), stock);
        }


        return Arrays.copyOf(calculatedMap.values().toArray(), calculatedMap.values().toArray().length, Stock[].class);
    }
}
