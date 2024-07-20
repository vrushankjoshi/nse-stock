package com.nse.connect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pojo.MarketState;
import com.pojo.StockName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ControllerAdvice
public class NSERetrieveAllEquityNames {


    private static final Logger logger = LogManager.getLogger(NSERetrieveAllEquityNames.class);

    Map<String, List<String>> headerFields = null;
    List<String> cookiesList = null;
    String cookies = null;

    public NSERetrieveAllEquityNames() {


        try {
            URL obj = new URL("https://www.nseindia.com");
            connection = (HttpURLConnection) obj.openConnection();


            connection.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("method", "GET");

            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            //connection.connect();
            //connection.getContent();
            cookies = connection.getHeaderField("Set-Cookie");
            //System.out.println("Cookies -->" + cookies + "<--");
            //System.out.println("Response code -->" + connection.getResponseCode() + "<--");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callLiveEquityMarketLink() {


        try {
            URL obj = new URL("https://www.nseindia.com/market-data/live-equity-market");
            connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestProperty("authority", "www.nseindia.com");
            connection.setRequestProperty("path", "/market-data/live-equity-market");
            connection.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("method", "GET");

            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

            cookies = connection.getHeaderField("Set-Cookie");
            //System.out.println("Cookies -->" + cookies + "<--");
            //System.out.println( "Response code -->" + connection.getResponseCode()+ "<--");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpURLConnection connection = null;


    public String buildURL(String symbol) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append("https://www.nseindia.com/api/equity-stockIndices?");
        builder.append("index=").append(symbol);

        return builder.toString();
    }


    @ResponseBody
    public List getStockNames(String myURL) throws IOException {
        callLiveEquityMarketLink();
        connectNSE(buildURL(myURL));
        List list = getResponse();

        return list;
    }

    public String getLastWorkingDay() throws IOException, ParseException {


        connectNSE("https://www.nseindia.com/api/marketStatus");

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //System.out.println(sb.toString());

        JSONObject unformattedJSONResponse = new JSONObject(sb.toString());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(unformattedJSONResponse.get("marketState").toString());
        String prettyJsonString = gson.toJson(jsonElement);
        //System.out.println(prettyJsonString);

        ObjectMapper mapper = new ObjectMapper();
        MarketState[] gsonList = mapper.readValue(prettyJsonString, MarketState[].class);
        SimpleDateFormat lFormatter = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date dt = null;
        String lastWorkingDay = null;
        for (MarketState stockState : gsonList) {


            if (stockState != null && stockState.getMarket() != null &&
                    stockState.getMarket().equalsIgnoreCase("Capital Market")) {
                dt = lFormatter.parse(stockState.getTradeDate().split(" ")[0]);
                lastWorkingDay = newFormat.format(dt);
                break;
            }


        }

        System.out.println(lastWorkingDay);

        return lastWorkingDay;


    }


    public boolean connectNSE(String myURL) {

        try {

            System.out.println(myURL);
            URL obj = new URL(myURL);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestProperty("authority", "www.nseindia.com");
            connection.setRequestProperty("method", "GET");
            connection.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
            //connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml,application/json;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            //connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("method", "GET");
            //connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Cookie", cookies);
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

            //connection.connect();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public ArrayList<String> getResponse() throws IOException {


        ArrayList<String> quoteNameList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //System.out.println(sb.toString());

        JSONObject unformattedJSONResponse = new JSONObject(sb.toString());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(unformattedJSONResponse.get("data").toString());
        String prettyJsonString = gson.toJson(jsonElement);
        //System.out.println(prettyJsonString);

        ObjectMapper mapper = new ObjectMapper();
        StockName[] gsonList = mapper.readValue(prettyJsonString, StockName[].class);
        for (StockName stockName : gsonList) {

            quoteNameList.add(stockName.getSymbol());
            //System.out.println(stockName.getSymbol());
        }
        Collections.sort(quoteNameList);
        return quoteNameList;
    }

    public static void main(String[] args) throws IOException {

        NSERetrieveAllEquityNames conn = new NSERetrieveAllEquityNames();

        try {
            conn.getLastWorkingDay();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        /*conn.callLiveEquityMarketLink();
        conn.connectNSE(conn.buildURL("NIFTY%2050"));
        conn.getResponse();*/


    }
}