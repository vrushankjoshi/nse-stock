package com.nse.connect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nse.calculation.HigherHigh;
import com.nse.calculation.RSI;
import com.nse.calculation.StockDetailsMap;
import com.nse.constant.HighLowPoint;
import com.nse.domain.StockDomainDetails;
import com.pojo.Stock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class NSERetrieveEquityDetails extends StockDomainDetails {

    private static final Logger logger = LogManager.getLogger(NSERetrieveEquityDetails.class);

    private String symbol = null;
    private String from = null;
    private String to = null;

    private HttpURLConnection connection = null;

    CookieManager cookieManager = null;

    private Stock[] gsonList = null;

    private List<HttpCookie> cookieList = null;




    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }







    public Stock[] mapToArray(HashMap myRsiMap) {
        Stock[] response = new Stock[myRsiMap.size()];
        Object[] objs = myRsiMap.values().toArray();

        for (int i = 0; i < objs.length; i++) {
            response[i] = (Stock) objs[i];
        }

        return response;
    }

    public static <Arraylist> void main(String[] args) throws IOException, ParseException, InterruptedException {

        NSERetrieveEquityDetails conn = new NSERetrieveEquityDetails();

        String symbol = "UPL";
        String startDateStr = "01-07-2023";
        String endDateStr = "30-03-2024";
        int rsiLength = 14;
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

        HashMap myStockList = null;

        HigherHigh hh = new HigherHigh(rsiLength, myStockList);

        HashMap hList = hh.findPriceAction(myStockList, rsiLength, HighLowPoint.PRICE_AND_RSI);
        Stock stock = null;

        logger.info("================== higher high prices ================");
        conn.printTree((TreeMap) hList.get("PRICE_HIGHERS"));

        logger.info("================== Lower Low prices ================");
        conn.printTree((TreeMap) hList.get("PRICE_LOWERS"));

        //logger.info("================== higher high RSI ================");
        //conn.printTree((TreeMap) hList.get("RSI_HIGHERS"));

        //logger.info("================== Lower Low RSI ================");
        //conn.printTree((TreeMap) hList.get("RSI_LOWERS"));


    }

    public Stock findHgherRsiLowerPrice(String symbol, Stock[] response, int lastNoOfDays) {

        Stock prevStock = null;
        Stock currStock = null;
        boolean successFlag = true;
        int startPoint = 0;
        if (response.length > lastNoOfDays) {
            startPoint = response.length - lastNoOfDays;
            for (int i = response.length - 1; i > startPoint; i--) {
                //for (int i = startPoint +1; i < response.length; i++) {
                prevStock = response[i - 1];
                currStock = response[i];
                if (prevStock.getChClosingPrice() > currStock.getChClosingPrice() ||
                        prevStock.getRsi() > currStock.getRsi()) {
                    successFlag = false;
                    break;

                } else if (i == (response.length - 1) &&
                        (currStock.getRsi() < 65 || prevStock.getRsi() > 65)) {
                    successFlag = false;
                    break;
                }

            }
        } else {
            logger.error("Not Enough Elements for " + symbol);

        }

        if (successFlag)
            currStock = response[response.length - 1];
        else
            currStock = null;


        return currStock;
    }

    public void printTree(TreeMap treeMap) {
        NavigableSet nSet = treeMap.navigableKeySet();
        Long key = null;
        Stock stockDetail = null;

        for (Iterator it = nSet.iterator(); it.hasNext(); ) {
            key = (Long) it.next();
            stockDetail = (Stock) treeMap.get(key);
            logger.info(stockDetail.getChTimestamp() + ", " + stockDetail.getChClosingPrice() + ", " + stockDetail.getRsi());
        }
    }

}

