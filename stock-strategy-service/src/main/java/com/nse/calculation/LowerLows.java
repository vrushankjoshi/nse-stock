package com.nse.calculation;

import com.pojo.Stock;

import java.util.*;

public class LowerLows {

    private int length = 0;
    private HashMap stockDetailsMap;

    public LowerLows(int length, HashMap stockDetailsMap) {
        this.length = length;
        this.stockDetailsMap = stockDetailsMap;
    }


    private void findPeak() {
        Iterator<Map.Entry<java.sql.Date, Stock>> iterator = stockDetailsMap.entrySet().iterator();
        java.sql.Date hhDate = null;
        java.sql.Date hhTempDate = null;
        Stock hhTempStock = null;
        Stock hhStock = null;
        while (iterator.hasNext()) {
            Map.Entry<java.sql.Date, Stock> entry = iterator.next();
            hhTempDate = entry.getKey();
            hhTempStock = entry.getValue();

            if (hhDate == null) {
                hhDate = hhTempDate;
                hhStock = hhTempStock;
            } else {

            }
        }
    }


    public ArrayList findLowerLowsClosingPrice() {


        System.out.println("==================FINDING PRICE LOWER LOWS for Length " + length + "=======================");
        //System.out.println("stockDetailsMap size -->" + stockDetailsMap.size());
        TreeMap tMap = new TreeMap();
        tMap.putAll(stockDetailsMap);
        Object[] keys = (Object[]) tMap.keySet().toArray();

        Object[] sliceKeys = null;
        Stock stock = null;
        Stock nextStock = null;
        Stock tempStock = null;
        ArrayList lowerLowsList = new ArrayList();


        Stock prevGroupLowerLow = null;
        int i = 0;
        while (i < keys.length) {
            sliceKeys = Arrays.copyOfRange(keys, i, i + length);

            for (int j = 0; j < sliceKeys.length - 1; j++) {
                stock = (Stock) stockDetailsMap.get((Long) sliceKeys[j]);
                nextStock = (Stock) stockDetailsMap.get((Long) sliceKeys[j + 1]);
                if (nextStock == null) {
                    break;
                }
                //System.out.println("stock.date -->"+ stock.getChTimestamp() + "<--,  price-->" + stock.getChClosingPrice() + " , next stock.date -->" + nextStock.getChTimestamp()+ "<-- next price -->" + nextStock.getChClosingPrice() + "<--");
                if (j == 0) {
                    if (nextStock.getChClosingPrice() < stock.getChClosingPrice())
                        tempStock = nextStock;
                    else
                        tempStock = stock;
                } else {
                    if (tempStock.getChClosingPrice() < nextStock.getChClosingPrice())
                        tempStock = nextStock;
                    else if (tempStock.getChClosingPrice() < stock.getChClosingPrice())
                        tempStock = stock;
                }

            }
            if (prevGroupLowerLow == null || prevGroupLowerLow.getChClosingPrice() < tempStock.getChClosingPrice()) {
                prevGroupLowerLow = tempStock;
                lowerLowsList.add(prevGroupLowerLow);
            }

            i = i + length;

        }

        return lowerLowsList;
    }

    public ArrayList findRSILowerLows() {
        System.out.println("==================FINDING RSI LOWER LOWS for length " + length + "=======================");
        //System.out.println("stockDetailsMap size -->" + stockDetailsMap.size());
        TreeMap tMap = new TreeMap();
        tMap.putAll(stockDetailsMap);
        Object[] keys = (Object[]) tMap.keySet().toArray();

        Object[] sliceKeys = null;
        Stock stock = null;
        Stock nextStock = null;
        Stock tempStock = null;
        ArrayList lowerLowsList = new ArrayList();
        Stock prevGroupLowerLows = null;
        int i = 0;
        while (i < keys.length) {
            sliceKeys = Arrays.copyOfRange(keys, i, i + length);

            for (int j = 0; j < sliceKeys.length - 1; j++) {
                stock = (Stock) stockDetailsMap.get((Long) sliceKeys[j]);
                nextStock = (Stock) stockDetailsMap.get((Long) sliceKeys[j + 1]);
                if (nextStock == null) {
                    break;
                }
                //System.out.println("stock.date -->"+ stock.getChTimestamp() + "<--,  price-->" + stock.getChClosingPrice() + " , next stock.date -->" + nextStock.getChTimestamp()+ "<-- next price -->" + nextStock.getChClosingPrice() + "<--");
                if (j == 0) {
                    if (nextStock.getRsi() < stock.getRsi())
                        tempStock = nextStock;
                    else
                        tempStock = stock;
                } else {
                    if (tempStock.getRsi() < nextStock.getRsi())
                        tempStock = nextStock;
                    else if (tempStock.getRsi() < stock.getRsi())
                        tempStock = stock;
                }

            }
            if (prevGroupLowerLows == null || prevGroupLowerLows.getRsi() < tempStock.getRsi()) {
                prevGroupLowerLows = tempStock;
                lowerLowsList.add(prevGroupLowerLows);
            }

            i = i + length;

        }

        return lowerLowsList;
    }

}
