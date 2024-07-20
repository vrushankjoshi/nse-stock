package com.nse.calculation;

import com.nse.constant.HighLowPoint;
import com.pojo.Stock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class HigherHigh {

    private static final Logger logger = LogManager.getLogger(HigherHigh.class);
    private int length = 0;
    private HashMap stockDetailsMap;

    public HigherHigh(int length, HashMap stockDetailsMap) {
        this.length = length;
        this.stockDetailsMap = stockDetailsMap;
    }


    public HashMap findPriceAction(HashMap stockDetailsMap, int length, HighLowPoint point) {


        logger.info("================== FINDING PRICE ACTION for length " + length + " =======================");
        //System.out.println("stockDetailsMap size -->" + stockDetailsMap.size());
        TreeMap tMap = new TreeMap();
        tMap.putAll(stockDetailsMap);
        Object[] keys = (Object[]) tMap.keySet().toArray();
        //System.out.println("[findHigherHighClosingPrice] : keys.length -->" + keys.length);

        Object[] sliceKeys = null;


        TreeMap highTreeMap = new TreeMap();
        TreeMap lowTreeMap = new TreeMap<>();

        TreeMap highRsiTreeMap = new TreeMap();
        TreeMap lowRsiTreeMap = new TreeMap<>();
        int i = 0;

        Stock stock = null;
        Stock localHighStock = null;
        Stock localLowStock = null;

        Stock localHighRsiStock = null;
        Stock localLowRsiStock = null;

        while (i < keys.length) {

            if (i + length < keys.length)
                sliceKeys = Arrays.copyOfRange(keys, i, i + length);
            else
                sliceKeys = Arrays.copyOfRange(keys, i, keys.length);

            localHighStock = null;
            localLowStock = null;
            localHighRsiStock = null;
            localLowRsiStock = null;
            //System.out.println("*********************");
            for (int j = 0; j < sliceKeys.length; j++) {
                stock = (Stock) stockDetailsMap.get((Long) sliceKeys[j]);

                //System.out.println("[findHigherHighClosingPrice] : stock.date -->"+ stock.getChTimestamp() + "<--,  rsi-->" + stock.getRsi() );
                if (j == 0) {
                    localHighStock = stock.clone();
                    localLowStock = stock.clone();

                    localHighRsiStock = stock.clone();
                    localHighRsiStock.setRsi(0);
                    localLowRsiStock = stock.clone();
                    localLowRsiStock.setRsi(100);
                    //System.out.println("localHighStock first -->"+localHighRsiStock.getChTimestamp()+","+localHighRsiStock.getRsi());

                } else {
                    if (point == HighLowPoint.PRICE) {
                        if (stock.getChClosingPrice() < localLowStock.getChClosingPrice()) {
                            localLowStock = stock;

                        } else if (localHighStock.getChClosingPrice() < stock.getChClosingPrice()) {
                            localHighStock = stock;
                        }
                    } else if (point == HighLowPoint.RSI) {
                        if (stock.getRsi() < localLowRsiStock.getRsi()) {
                            localLowRsiStock = stock;

                        } else if (localHighRsiStock.getRsi() < stock.getRsi()) {
                            localHighRsiStock = stock;
                        }
                    } else if (point == HighLowPoint.PRICE_AND_RSI) {
                        if (stock.getChClosingPrice() < localLowStock.getChClosingPrice()) {
                            localLowStock = stock;

                        } else if (localHighStock.getChClosingPrice() < stock.getChClosingPrice()) {
                            localHighStock = stock;
                        }
                        if (stock.getRsi() < localLowRsiStock.getRsi()) {
                            localLowRsiStock = stock;

                        } else if (localHighRsiStock.getRsi() < stock.getRsi()) {
                            localHighRsiStock = stock;
                        }
                    }

                }
            }


            //System.out.println("%%%%%%%%%%%%%%%%%%%%%%");
            //System.out.println("localHighRsiStock day stock --> "+localHighRsiStock.getChTimestamp() + " , " + localHighRsiStock.getRsi());
            //System.out.println("localLowRsiStock day stock --> "+localLowRsiStock.getChTimestamp() + " , " + localLowRsiStock.getRsi());
            if (point == HighLowPoint.PRICE_AND_RSI) {
                //Add for PRICE
                if (!highTreeMap.containsKey(localHighStock.getChTimestamp().getTime()))
                    highTreeMap.put(localHighStock.getChTimestamp().getTime(), localHighStock);
                if (!lowTreeMap.containsKey(localLowStock.getChTimestamp().getTime()))
                    lowTreeMap.put(localLowStock.getChTimestamp().getTime(), localLowStock);


                //Add for RSI - if its not for the first set of data as its's RSI would be zero
                if ((i != 0) && !highRsiTreeMap.containsKey(localHighRsiStock.getChTimestamp().getTime()))
                    highRsiTreeMap.put(localHighRsiStock.getChTimestamp().getTime(), localHighRsiStock);
                if ((i != 0) && !lowRsiTreeMap.containsKey(localLowRsiStock.getChTimestamp().getTime()))
                    lowRsiTreeMap.put(localLowRsiStock.getChTimestamp().getTime(), localLowRsiStock);


            } else if (point == HighLowPoint.PRICE) {

                if (!highTreeMap.containsKey(localHighStock.getChTimestamp().getTime()))
                    highTreeMap.put(localHighStock.getChTimestamp().getTime(), localHighStock);
                if (!lowTreeMap.containsKey(localLowStock.getChTimestamp().getTime()))
                    lowTreeMap.put(localLowStock.getChTimestamp().getTime(), localLowStock);

            } else if (point == HighLowPoint.RSI) {

                if ((i != 0) && !highRsiTreeMap.containsKey(localHighRsiStock.getChTimestamp().getTime()))
                    highRsiTreeMap.put(localHighRsiStock.getChTimestamp().getTime(), localHighRsiStock);
                if ((i != 0) && !lowRsiTreeMap.containsKey(localLowRsiStock.getChTimestamp().getTime()))
                    lowRsiTreeMap.put(localLowRsiStock.getChTimestamp().getTime(), localLowRsiStock);

            }


            //i++;
            i = i + length;
        }


        Stock highestDayStock = null;
        //highTreeMap.forEach((key, value) -> System.out.println(value));
        highestDayStock = highestLowestDay(highTreeMap, HighLowPoint.HIGHEST, HighLowPoint.PRICE);
        if (highestDayStock != null)
            logger.info("Highest day stock price --> " + highestDayStock.getChTimestamp() + " , " + highestDayStock.getChClosingPrice());

        //lowTreeMap.forEach((key, value) -> System.out.println(value));
        Stock lowestDayStock = null;
        lowestDayStock = highestLowestDay(lowTreeMap, HighLowPoint.LOWEST, HighLowPoint.PRICE);
        if (lowestDayStock != null)
            logger.info("Lowest day stock price --> " + lowestDayStock.getChTimestamp() + " , " + lowestDayStock.getChClosingPrice());

        Stock[] tempResponse = null;
        tempResponse = Arrays.copyOf(highTreeMap.values().toArray(), highTreeMap.values().toArray().length, Stock[].class);
        List tempList = null;
        tempList = Arrays.asList(tempResponse);
        Collections.sort(tempList);


        //printTree( highTreeMap);
        logger.info("<========== making higher high =========>");
        //logger.info("lowest date -->" + lowestDayStock.getChTimestamp() + " , " + lowestDayStock.getChClosingPrice());
        highTreeMap = higherHighPriceFromLowestPoint(tempList, lowestDayStock);
        logger.info("<========== END making higher high =========>");

        tempResponse = Arrays.copyOf(lowTreeMap.values().toArray(), lowTreeMap.values().toArray().length, Stock[].class);
        tempList = Arrays.asList(tempResponse);
        Collections.sort(tempList);
        lowTreeMap = lowerLowPriceFromHighestPoint(tempList, highestDayStock);

        //highRsiTreeMap.forEach((key, value) -> System.out.println(value));
        highestDayStock = highestLowestDay(highRsiTreeMap, HighLowPoint.HIGHEST, HighLowPoint.RSI);
        if (highestDayStock != null)
            logger.info("Highest day stock RSI --> " + highestDayStock.getChTimestamp() + " , " + highestDayStock.getRsi());

        //lowRsiTreeMap.forEach((key, value) -> System.out.println(value));
        lowestDayStock = highestLowestDay(lowRsiTreeMap, HighLowPoint.LOWEST, HighLowPoint.RSI);
        if (lowestDayStock != null)
            logger.info("Lowest day stock RSI --> " + lowestDayStock.getChTimestamp() + " , " + lowestDayStock.getRsi());


        //System.out.println("==================");

        HashMap map = new HashMap();
        map.put("PRICE_HIGHERS", highTreeMap);
        map.put("PRICE_LOWERS", lowTreeMap);
        map.put("RSI_HIGHERS", highRsiTreeMap);
        map.put("RSI_LOWERS", lowRsiTreeMap);

        return map;
    }

    public TreeMap higherHighPriceFromLowestPoint(List tempList, Stock lowestDayStock) {
        Stock tempStock = null;
        Stock prevHigh = null;
        TreeMap highTreeMap = new TreeMap();

        //printTree( highTreeMap);
        //logger.info("<========== making higher high =========>");
        //logger.info("lowest date -->" + lowestDayStock.getChTimestamp() + " , " + lowestDayStock.getChClosingPrice());
        for (int i = 0; i < tempList.size(); i++) {
            tempStock = (Stock) tempList.get(i);
            //logger.info("lowest date -->" + lowestDayStock.getChTimestamp() + " curr->" + tempStock.getChTimestamp() );
            if (tempStock.getChTimestamp().after(lowestDayStock.getChTimestamp())) {
                //logger.info("FOUND date -->" + tempStock.getChTimestamp() + " , " + tempStock.getChClosingPrice());
                if (prevHigh == null) {
                    prevHigh = tempStock;
                    highTreeMap.put(prevHigh.getChTimestamp().getTime(), prevHigh);
                    //logger.info("high date -->" + prevHigh.getChTimestamp() + " , " + prevHigh.getChClosingPrice());
                } else {
                    if (tempStock.getChClosingPrice() > prevHigh.getChClosingPrice()) {
                        prevHigh = tempStock;
                        highTreeMap.put(prevHigh.getChTimestamp().getTime(), prevHigh);
                        //logger.info("high date -->" + prevHigh.getChTimestamp() + " , " + prevHigh.getChClosingPrice());
                    }
                }
            }
        }

        return highTreeMap;
    }

    public TreeMap lowerLowPriceFromHighestPoint(List tempList, Stock highestDayStock) {
        Stock tempStock = null;
        Stock prevLow = null;
        TreeMap lowTreeMap = new TreeMap();

        //printTree( highTreeMap);
        logger.info("<========== /////////// making lower low /////////// =========>");
        //logger.info("lowest date -->" + lowestDayStock.getChTimestamp() + " , " + lowestDayStock.getChClosingPrice());
        for (int i = 0; i < tempList.size(); i++) {
            tempStock = (Stock) tempList.get(i);
            logger.info("highestDayStock date -->" + highestDayStock.getChTimestamp() + ", " + highestDayStock.getChClosingPrice() + " tempStock date -->" + tempStock.getChTimestamp() + " , " + tempStock.getChClosingPrice());
            if (tempStock.getChTimestamp().after(highestDayStock.getChTimestamp())) {
                //logger.info("FOUND date -->" + tempStock.getChTimestamp() + " , " + tempStock.getChClosingPrice());
                if (prevLow == null) {
                    prevLow = tempStock;
                    lowTreeMap.put(prevLow.getChTimestamp().getTime(), prevLow);
                    logger.info("FOUND  low date -->" + prevLow.getChTimestamp() + " , " + prevLow.getChClosingPrice());
                } else {
                    if (tempStock.getChClosingPrice() < prevLow.getChClosingPrice()) {
                        prevLow = tempStock;
                        lowTreeMap.put(prevLow.getChTimestamp().getTime(), prevLow);
                        logger.info("FOUND low date -->" + prevLow.getChTimestamp() + " , " + prevLow.getChClosingPrice());
                    }
                }
            }
        }

        logger.info("<========== /////////// making lower low /////////// =========>");

        return lowTreeMap;
    }


    private Stock highestLowestDay(TreeMap highTreeMap, HighLowPoint point, HighLowPoint priceAction) {
        logger.info("Start searching for " + point + " " + priceAction);
        Stock stock = null;
        Stock pointDayStock = null;

        for (Object entry : highTreeMap.entrySet()) {
            Map.Entry me = (Map.Entry) entry;
            Long key = (Long) me.getKey();
            stock = (Stock) me.getValue();

            switch (priceAction) {
                case RSI:
                    if (HighLowPoint.HIGHEST == point) {
                        if (pointDayStock == null || (pointDayStock.getRsi() < stock.getRsi()))
                            pointDayStock = stock;
                    } else {
                        if (pointDayStock == null || (pointDayStock.getRsi() > stock.getRsi()))
                            pointDayStock = stock;
                    }
                    break;
                case PRICE:
                    if (HighLowPoint.HIGHEST == point) {
                        if (pointDayStock == null || (pointDayStock.getChClosingPrice() < stock.getChClosingPrice()))
                            pointDayStock = stock;
                    } else {
                        if (pointDayStock == null || (pointDayStock.getChClosingPrice() > stock.getChClosingPrice()))
                            pointDayStock = stock;
                    }
                    break;
                case PRICE_AND_RSI:
                    break;
            }
            //System.out.println(stock.getChTimestamp()+","+stock.getChClosingPrice());


        }

        return pointDayStock;
    }


    public ArrayList findRSIHigherHigh() {
        logger.info("==================FINDING RSI HIGHER HIGHS for length " + length + "=======================");
        //System.out.println("stockDetailsMap size -->" + stockDetailsMap.size());
        TreeMap tMap = new TreeMap();
        tMap.putAll(stockDetailsMap);
        Object[] keys = (Object[]) tMap.keySet().toArray();

        Object[] sliceKeys = null;
        Stock stock = null;
        Stock nextStock = null;
        Stock tempStock = null;
        ArrayList higherHighList = new ArrayList();
        Stock prevGroupHigherHigh = null;
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
                    if (nextStock.getRsi() > stock.getRsi())
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
            if (prevGroupHigherHigh == null || prevGroupHigherHigh.getRsi() < tempStock.getRsi()) {
                prevGroupHigherHigh = tempStock;
                higherHighList.add(prevGroupHigherHigh);
            }

            i = i + length;

        }

        return higherHighList;
    }


}
