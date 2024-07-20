package com.nse.calculation;

import com.pojo.Stock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;

public class RSI {
    private static final Logger logger = LogManager.getLogger(RSI.class);

    private final int rsiLength;
    private final long firstDay;
    private final long lastDay;

    private float multiplier = 0;

    public RSI(int rsiLength, NavigableMap myRsiMap, long firstDay, long lastDay) {

        this.rsiLength = rsiLength;
        this.myRsiMap = myRsiMap;
        this.firstDay = firstDay;
        this.lastDay = lastDay;

        multiplier = 2 / (float) (rsiLength + 1);
    }


    NavigableMap myRsiMap = null;


    public HashMap calculateRSI() {

        TreeMap tMap = new TreeMap();
        tMap.putAll(myRsiMap);
        Object[] keys = tMap.keySet().toArray();


        Stock stock = null;
        int jStartLocation = 0;
        int jEndLocation = 0;
        Stock currentDayStock = null;
        Stock previousDayStock = null;
        double rsi = 0;
        float positive = 0.0F;
        float nagative = 0.0F;
        long key = 0;

        //logger.debug("Total Keys -->" + keys.length);
        DecimalFormat df = new DecimalFormat("00.00");
        for (int i = rsiLength; i < keys.length; i++) {
            key = (long) keys[i];
            stock = (Stock) myRsiMap.get(key);
            jStartLocation = Math.max(i - rsiLength, 0);
            if (jStartLocation == 0)
                jEndLocation = i;
            else {
                jEndLocation = i + 1;
            }
            positive = 0.0F;
            nagative = 0.0F;
            if (jStartLocation == 0) {
                for (int j = jStartLocation; j < jEndLocation; j++) {
                    if (j == 0) {
                        continue;
                    }

                    //System.out.println(" i-jStartLocation -->" + i + " , jEndLocation-->" + jEndLocation + " , jEndLocation+rsiLength-->"+(jEndLocation+rsiLength) + " j-->"+j);
                    previousDayStock = (Stock) myRsiMap.get(keys[j - 1]);
                    currentDayStock = (Stock) myRsiMap.get(keys[j]);

                    if (currentDayStock == null) {
                        logger.trace(jEndLocation);
                    }

                    //System.out.println("previousDayStock -->" + previousDayStock.getChTimestamp() + ", price -->" + previousDayStock.getChClosingPrice());
                    //System.out.println("currentDayStock -->" + currentDayStock.getChTimestamp() + ", price -->" + currentDayStock.getChClosingPrice());

                    if (currentDayStock.getChClosingPrice() > previousDayStock.getChClosingPrice()) {
                        positive += (currentDayStock.getChClosingPrice() - previousDayStock.getChClosingPrice());

                    } else {
                        nagative += (previousDayStock.getChClosingPrice() - currentDayStock.getChClosingPrice());
                    }
                    //System.out.println( "j -->" + j + ", RSI -->" + currentDayStock.getChTimestamp() + "," + currentDayStock.getChClosingPrice() + "," + df.format(rsi));

                }


                //rsi = calculatePreRSI(currentDayStock, positive,nagative, rsiLength,firstTimeFlag);

                currentDayStock.setAvgPositivePerRsi(positive / rsiLength);
                currentDayStock.setAvgNegativePerRsi(nagative / rsiLength);
                stock.setAvgPositivePerRsi(currentDayStock.getAvgPositivePerRsi());
                stock.setAvgNegativePerRsi(currentDayStock.getAvgNegativePerRsi());
                //System.out.println( "i -->" + i + ", RSI -->" + stock.getChTimestamp() + "," + stock.getChClosingPrice());

            } else {
                previousDayStock = (Stock) myRsiMap.get(keys[i - 1]);
                currentDayStock = (Stock) myRsiMap.get(keys[i]);
                if (currentDayStock.getChClosingPrice() > previousDayStock.getChClosingPrice()) {
                    positive = previousDayStock.getAvgPositivePerRsi() * (rsiLength - 1) + (currentDayStock.getChClosingPrice() - previousDayStock.getChClosingPrice());
                } else {
                    positive = previousDayStock.getAvgPositivePerRsi() * (rsiLength - 1);
                }

                if (currentDayStock.getChClosingPrice() < previousDayStock.getChClosingPrice()) {
                    nagative = previousDayStock.getAvgNegativePerRsi() * (rsiLength - 1) + (previousDayStock.getChClosingPrice() - currentDayStock.getChClosingPrice());
                } else {
                    nagative = previousDayStock.getAvgNegativePerRsi() * (rsiLength - 1);
                }

                currentDayStock.setAvgPositivePerRsi(positive / rsiLength);
                currentDayStock.setAvgNegativePerRsi(nagative / rsiLength);

                stock.setAvgPositivePerRsi(currentDayStock.getAvgPositivePerRsi());
                stock.setAvgNegativePerRsi(currentDayStock.getAvgNegativePerRsi());
            }
            rsi = calculatePreRSI(currentDayStock, rsiLength);

            //System.out.println( "i -->" + i + ", RSI -->" + stock.getChTimestamp() + "," + stock.getChClosingPrice() + "," + df.format(rsi));
            stock.setRsi(Double.valueOf(df.format(rsi)));
            myRsiMap.put(key, stock);


        }
        HashMap map = new HashMap();
        map.putAll(myRsiMap);
        return map;
    }

    private double calculatePreRSI(Stock currentDayStock, int rsiLength) {
        float positiveAvg = 0;
        float nagativeAvg = 0;
        double rsi;


        positiveAvg = currentDayStock.getAvgPositivePerRsi() / rsiLength;
        nagativeAvg = currentDayStock.getAvgNegativePerRsi() / rsiLength;

        //double rs = (nagativeAvg <= 0) ? 0 : (positiveAvg / nagativeAvg);
        double rs = positiveAvg / nagativeAvg;
        //System.out.println("positiveAvg -->" + positiveAvg + ", nagativeAvg -->" + nagativeAvg + ", rs -->" + rs);
        rsi = 100 - (100 / (1 + rs));
        return rsi;
    }


}
