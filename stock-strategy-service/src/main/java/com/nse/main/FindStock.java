package com.nse.main;

import com.nse.connect.NSERetrieveAllEquityNames;
import com.nse.constant.StockFrequency;
import com.nse.strategy.BulishStockStrategy;
import com.pojo.Stock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class FindStock {

    private static final Logger logger = LogManager.getLogger(FindStock.class);

    public static void main(String args[]) {
        NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();

        /*

        NIFTY%20100
        NIFTY%20200
        NIFTY%20MIDCAP%20100
        NIFTY%20MIDCAP%20150

         */
        ArrayList<String> list = null;
        try {
            names.callLiveEquityMarketLink();
            names.connectNSE(names.buildURL("NIFTY%20200"));
            list = names.getResponse();
            if (list != null) {
                list.remove("NIFTY 200");
            }

            names.connectNSE(names.buildURL("NIFTY%20MIDCAP%20150"));
            ArrayList<String> templist = names.getResponse();
            if (templist != null) {
                templist.remove("NIFTY MIDCAP 150");
            }

            list.addAll(templist);
            list = (ArrayList<String>) list.stream().distinct().collect(Collectors.toList());
            Collections.sort(list);
            logger.info("Find Bullish Stocks from " + list.size() + " no. of shares.");

            //retrieveEquityDtls = new NSERetrieveEquityDetails();
            int rsiLength = 3;
            String symbol = null;


            DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

            Date currentDate = new Date();
            String startDateStr = "01-01-2023";
            String endDateStr = lFormatter.format(currentDate);
            //"27-03-2024";
            logger.info(list);
            Stock stock = null;
            BulishStockStrategy bullish = new BulishStockStrategy();

            for (int i = 0; i < list.size(); i++) {
                symbol = (String) list.get(i);
                logger.info("i -->" + i + ", symbol -->" + symbol);

                stock = bullish.bulishSignal(symbol, startDateStr, endDateStr, rsiLength, StockFrequency.WEEKLY);

                if (stock != null) {
                    logger.info("i -->" + i + ", Bullish -->" + symbol + " , Date -->" + stock.getChTimestamp() + ", current Price -->" + stock.getChClosingPrice() + ", rsi -->" + stock.getRsi() + ", 52Week -->" + stock.getCh52WeekHighPrice());
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
