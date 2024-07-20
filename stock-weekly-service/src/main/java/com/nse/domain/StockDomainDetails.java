package com.nse.domain;

import com.pojo.Stock;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

public abstract class StockDomainDetails {

    public HashMap retriveStockAllDetails(String symbol, String startDateStr, String endDateStr, int rsiLength, int rsiSmoothingLength) throws ParseException, IOException {

        Stock[] response = retrieveStockDtls(symbol, startDateStr, endDateStr);
        response = calculateSmaEma(response, rsiSmoothingLength);
        HashMap myStockList = calculateRSI(response, rsiLength);
        return myStockList;
    }

    public HashMap calculateSmaEmaRSI(Stock[] response, int rsiLength, int rsiSmoothingLength) {
        response = calculateSmaEma(response, rsiSmoothingLength);
        HashMap myStockList = calculateRSI(response, rsiLength);
        return myStockList;
    }

    public abstract Stock[] retrieveStockDtls(String symbol, String startDateStr, String endDateStr) throws ParseException, IOException;

    public abstract Stock[] calculateSmaEma(Stock[] response, int rsiLength);

    public abstract HashMap calculateRSI(Stock[] response, int rsiLength);

}
