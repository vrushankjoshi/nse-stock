package com.nse.service;

import com.dao.StockEntity;

import java.text.ParseException;
import java.util.List;

public interface WeeklyStockService {

    public void createWeeklyStocks(String stockSymbol) throws RuntimeException, ParseException;

    public void buildWeeklyStockData() throws ParseException;
}
