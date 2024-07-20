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

    public void callLiveEquityMarketLink() {


        try {
            URL obj = new URL("https://www.nseindia.com/market-data/live-equity-market");
            //System.out.println(obj.toString());
            connection = (HttpURLConnection) obj.openConnection();


            connection.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                            + " Chrome/89.0.4389.114 Safari/537.36");
            connection.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");


            connection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));
            //connection.getContent();

            cookieList = cookieManager.getCookieStore().getCookies();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callNSEgetQuoate(String symbol) throws IOException, InterruptedException {


        URL obj = new URL("https://www.nseindia.com/api/historical/cm/equity?symbol=" + symbol);

        connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        //connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Cache-Control", "max-age=0");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                        + " Chrome/89.0.4389.114 Safari/537.36");
        connection.setRequestProperty(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));


        //connection.getContent();

        cookieList = cookieManager.getCookieStore().getCookies();


        //cookies = connection.getHeaderField("Set-Cookie");
        //System.out.println("Cookies -->" + cookies + "<--");
        //System.out.println( "Response code -->" + connection.getResponseCode()+ "<--");

    }

    public NSERetrieveEquityDetails() {


        try {
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            URL obj = new URL("https://www.nseindia.com");
            //System.out.println(obj.toString());
            connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                            + " Chrome/89.0.4389.114 Safari/537.36");
            connection.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.getContent();
            cookieList = cookieManager.getCookieStore().getCookies();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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

    public String buildURL(String symbol, String from, String to) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://www.nseindia.com/api/historical/cm/equity?");
        builder.append("symbol=").append(URLEncoder.encode(symbol));
        builder.append("&from=").append(from);
        builder.append("&to=").append(to);

        //System.out.println(builder.toString());
        return builder.toString();
    }

    public boolean retriveStockDetailsByDates(String symbol, String from, String to) {

        try {

            String myURL = buildURL(symbol, from, to);
            URL obj = new URL(myURL);
            //logger.info(obj.toString());
            connection = (HttpURLConnection) obj.openConnection();
            if (from.equalsIgnoreCase(to))
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", cookieList.stream().filter(Objects::nonNull)
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining(";")));
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                            + " Chrome/89.0.4389.114 Safari/537.36");
            connection.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            cookieList = cookieManager.getCookieStore().getCookies();
            //Thread.sleep(1000);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Stock[] getResponse() throws IOException {

        InputStream in = connection.getInputStream();

        //BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //System.out.println(sb.toString());

        /*JSONArray jsonArray = new JSONArray(sb.toString());
        jsonArray.getJSONObject(0);*/


        JSONObject unformattedJSONResponse = new JSONObject(sb.toString());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(unformattedJSONResponse.get("data").toString());
        String prettyJsonString = gson.toJson(jsonElement);
        //System.out.println(prettyJsonString);

        ObjectMapper mapper = new ObjectMapper();
        gsonList = mapper.readValue(prettyJsonString, Stock[].class);

        Stock stock = null;
        for (int i = 0; i < gsonList.length; i++) {
            stock = gsonList[i];
            //System.out.println(" i -->" + i + ", date -->" + stock.getChTimestamp() + ", price -->" + stock.getChClosingPrice());
        }

        return gsonList;
    }

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
        try {
            for (int i = 0; i < rsiLength; i++) {
                stock = (Stock) myMap.get((Long) keys[i]);
                calculatedMap.put(stock.getChTimestamp(), stock);
            }
        } catch (Exception e) {
            System.out.println("key size -->" + keys.length);
            e.printStackTrace();
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

    public HashMap calculateRSI(Stock[] response, int rsiLength) {
        Stock stock = null;
        //StockDetailsMap myRsiMap = new StockDetailsMap(3);
        NavigableMap myRsiMap = new StockDetailsMap();

        for (Stock stockDetail : response) {
            //System.out.println(stockDetail.getChTimestamp()+", "+stockDetail.getChClosingPrice()+", "+stockDetail.getChPreviousClsPrice());
            myRsiMap.put(stockDetail.getChTimestamp().getTime(), stockDetail);

        }

        SortedSet<Long> keys = new TreeSet<Long>(myRsiMap.keySet()).descendingSet();
        long key = 0;
        RSI rsi = new RSI(rsiLength, myRsiMap, keys.first().longValue(), keys.last().longValue());
        HashMap myStockList = (HashMap) rsi.calculateRSI();


        return myStockList;

    }

    public Stock[] retrieveStockDtls(String symbol, String startDateStr, String endDateStr) throws ParseException, IOException {
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        java.sql.Date startDate = new java.sql.Date(lFormatter.parse(startDateStr).getTime());
        java.sql.Date endDate = new java.sql.Date(lFormatter.parse(endDateStr).getTime());
        Stock[] tempResponse = null;
        Stock[] response = null;
        /////
        java.sql.Date tempStartDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate ldStartDate = LocalDate.parse(startDateStr, formatter);
        LocalDate ldEndDate = LocalDate.parse(endDateStr, formatter);
        long days = ChronoUnit.DAYS.between(ldStartDate, ldEndDate);
        if (days < 90) {
            //logger.info("less than 90 days");
            retriveStockDetailsByDates(symbol, startDateStr, endDateStr);
            response = getResponse();

        } else {
            ldStartDate = ldEndDate.minusDays(90);
            while (true) {

                retriveStockDetailsByDates(symbol, formatter.format(ldStartDate), formatter.format(ldEndDate));
                tempResponse = getResponse();
                response = append(response, tempResponse);
                ldEndDate = ldStartDate;
                if (ldEndDate.isBefore(startDate.toLocalDate()) || ldEndDate.isEqual(startDate.toLocalDate())) {
                    break;
                }
                ldStartDate = ldEndDate.minusDays(90);

                if (ldStartDate.isBefore(startDate.toLocalDate())) {
                    ldStartDate = startDate.toLocalDate();
                }

            }
        }

        return response;
    }

    private Stock[] append(Stock[] a1, Stock[] a2) {
        if (a1 == null)
            return a2;

        Stock[] ret = new Stock[a1.length + a2.length];
        System.arraycopy(a1, 0, ret, 0, a1.length);
        System.arraycopy(a2, 0, ret, a1.length, a2.length);
        return ret;
    }

    public Stock[] mapToArray(HashMap myRsiMap) {
        Stock[] response = new Stock[myRsiMap.size()];
        Object[] objs = myRsiMap.values().toArray();

        for (int i = 0; i < objs.length; i++) {
            response[i] = (Stock) objs[i];
        }

        return response;
    }

    public HashMap arrayToMap(Stock[] response) {

        HashMap myRsiMap = new HashMap();
        for (Stock stockDetail : response) {
            //System.out.println(stockDetail.getChTimestamp()+", "+stockDetail.getChClosingPrice()+", "+stockDetail.getChPreviousClsPrice());
            myRsiMap.put(stockDetail.getChTimestamp().getTime(), stockDetail);

        }
        return myRsiMap;
    }


    public static <Arraylist> void main(String[] args) throws IOException, ParseException, InterruptedException {

        NSERetrieveEquityDetails conn = new NSERetrieveEquityDetails();
        conn.callLiveEquityMarketLink();
        String symbol = "UPL";
        String startDateStr = "01-07-2023";
        String endDateStr = "30-03-2024";
        int rsiLength = 14;
        int rsiSmoothingLength = 14;
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        java.sql.Date startDate = new java.sql.Date(lFormatter.parse(startDateStr).getTime());
        java.sql.Date endDate = new java.sql.Date(lFormatter.parse(endDateStr).getTime());

        HashMap myStockList = conn.retriveStockAllDetails(symbol, startDateStr, endDateStr, rsiLength, rsiSmoothingLength);

        Stock[] response = Arrays.copyOf(myStockList.values().toArray(), myStockList.values().toArray().length, Stock[].class);
//        WeeklyStock weeklyStock = new WeeklyStock(startDate,endDate,symbol,response);
//        myStockList = weeklyStock.buildWeeklyStockDetails();
//        response = conn.mapToArray(myStockList);
//        myStockList = conn.calculateSmaEmaRSI(response,rsiLength, rsiSmoothingLength);
        conn.findHgherRsiLowerPrice(symbol, response, rsiLength);

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

