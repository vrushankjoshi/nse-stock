package com.nse.strategy;

import com.nse.calculation.PriceAction;
import com.nse.calculation.WeeklyStock;
import com.nse.connect.NSERetrieveEquityDetails;
import com.nse.constant.HighLowPoint;
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
import java.util.stream.Collectors;

@ControllerAdvice
public class PriceActionStrategy {

    private static final Logger logger = LogManager.getLogger(PriceActionStrategy.class);
    private NSERetrieveEquityDetails retrieveEquityDetails;

    public PriceActionStrategy() {
        retrieveEquityDetails = new NSERetrieveEquityDetails();

    }

    public List findPriceHigherHigh(String symbol, String from, String to, int length, StockFrequency frequency, com.nse.constant.PriceAction priceActionConstant) throws ParseException, IOException {

        Stock stock = null;
        Stock[] response = null;

        List tempList = null;
        HashMap myStockList = null;
        WeeklyStock weeklyStock = null;

        symbol = URLEncoder.encode(symbol);

        NSERetrieveEquityDetails conn = new NSERetrieveEquityDetails();
        //myStockList = retrieveEquityDetails.retriveStockAllDetails(symbol, from, to, length, length);


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

            //myStockList = retrieveEquityDetails.calculateSmaEmaRSI(response, length, length);
            response = retrieveEquityDetails.mapToArray(myStockList);
            tempList = Arrays.asList(response);
            Collections.sort(tempList);
            response = Arrays.copyOf(tempList.toArray(), tempList.toArray().length, Stock[].class);
        }

        HashMap map = createMap(response);

        PriceAction action = new PriceAction();
        HashMap responseMap = action.buildPriceAction(map, length, HighLowPoint.PRICE);

        TreeMap actionMap = null;
        if (priceActionConstant == com.nse.constant.PriceAction.HIGHER_HIGH) {
            actionMap = (TreeMap) responseMap.get("PRICE_HIGHERS");
            tempList = (List) actionMap.values().stream().collect(Collectors.toList());
            logger.info("Higher High Price size -->" + tempList.size());
        } else if (priceActionConstant == com.nse.constant.PriceAction.LOWER_LOW) {
            actionMap = (TreeMap) responseMap.get("PRICE_LOWERS");
            tempList = (List) actionMap.values().stream().collect(Collectors.toList());
            logger.info("Lower Low Price size -->" + tempList.size());
        }
        return tempList;

    }

    private HashMap createMap(Stock[] response) {
        HashMap map = new HashMap();
        Stock stock = null;
        for (int i = 0; i < response.length; i++) {
            stock = response[i];
            map.put(stock.getChTimestamp().getTime(), stock);
        }
        return map;
    }


}
