package com.nse.strategy;

import com.nse.calculation.WeeklyStock;
import com.nse.connect.NSERetrieveEquityDetails;
import com.nse.constant.StockFrequency;
import com.pojo.Stock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ControllerAdvice
public class BulishStockStrategy {

    private static final Logger logger = LogManager.getLogger(BulishStockStrategy.class);

    private NSERetrieveEquityDetails retrieveEquityDetails;

    public BulishStockStrategy() {
        retrieveEquityDetails = new NSERetrieveEquityDetails();
        retrieveEquityDetails.callLiveEquityMarketLink();

    }

    public Stock bulishSignal(String symbol, String from, String to, int length, StockFrequency frequency) throws IOException, ParseException {
        Stock stock = null;
        Stock[] response = null;

        List tempList = null;
        HashMap myStockList = null;
        WeeklyStock weeklyStock = null;

        symbol = URLEncoder.encode(symbol);

        NSERetrieveEquityDetails retrieveEquityDtls = new NSERetrieveEquityDetails();

        myStockList = retrieveEquityDtls.retriveStockAllDetails(symbol, from, to, length, length);


        response = Arrays.copyOf(myStockList.values().toArray(), myStockList.values().toArray().length, Stock[].class);
        tempList = Arrays.asList(response);
        Collections.sort(tempList);


        response = Arrays.copyOf(tempList.toArray(), tempList.toArray().length, Stock[].class);

        if (frequency == StockFrequency.WEEKLY) {
            DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

            weeklyStock = new WeeklyStock(new java.sql.Date(lFormatter.parse(from).getTime()), new java.sql.Date(lFormatter.parse(to).getTime()), symbol, response);
            myStockList = weeklyStock.buildWeeklyStockDetails();

            response = Arrays.copyOf(myStockList.values().toArray(), myStockList.values().toArray().length, Stock[].class);
            //logger.info("`weeklyStock : response.length -->" + response.length);

            myStockList = retrieveEquityDtls.calculateSmaEmaRSI(response, length, length);
            response = retrieveEquityDtls.mapToArray(myStockList);
            tempList = Arrays.asList(response);
            Collections.sort(tempList);
            response = Arrays.copyOf(tempList.toArray(), tempList.toArray().length, Stock[].class);
        }


        stock = retrieveEquityDtls.findHgherRsiLowerPrice(symbol, response, length + 1);
        if (stock != null) {
            logger.info("Bullish -->" + symbol + " , Date -->" + stock.getChTimestamp() + ", current Price -->" + stock.getChClosingPrice() + ", rsi -->" + stock.getRsi() + ", 52Week -->" + stock.getCh52WeekHighPrice());
        } else {
            logger.info(symbol + " is not bullish currently");
        }

        return stock;
    }


    public Map matchingDates(ArrayList stockList, Date start, Date end) {
        Map matchingSet = new HashMap();
        Stock stock = null;
        for (int i = 0; i < stockList.size(); i++) {
            stock = (Stock) stockList.get(i);
            if (stock.getChTimestamp().after(start) && stock.getChTimestamp().before(end)) {
                matchingSet.put(stock.getChTimestamp(), stock);
            }
        }
        return matchingSet;

    }

    private void printRSI(ArrayList hhStockPrice) {
        Stock stock = null;
        for (int j = 0; j < hhStockPrice.size(); j++) {
            stock = (Stock) hhStockPrice.get(j);
            System.out.println(stock.getChTimestamp() + " , " + stock.getRsi());
        }
    }

    private void printPrice(ArrayList llStockRsi) {
        Stock stock = null;
        for (int j = 0; j < llStockRsi.size(); j++) {
            stock = (Stock) llStockRsi.get(j);
            System.out.println(stock.getChTimestamp() + " , " + stock.getChClosingPrice());
        }
    }

    public static void main(String[] args) throws IOException, ParseException {

        BulishStockStrategy bullish = new BulishStockStrategy();
        ;
        Stock stock = bullish.bulishSignal("TCS", "01-11-2023", "20-03-2024", 10, StockFrequency.WEEKLY);
        if (stock != null)
            System.out.println("Date -->" + stock.getChTimestamp() + " , rsi -->" + stock.getRsi() + ", price -->" + stock.getChClosingPrice());
    }
}
