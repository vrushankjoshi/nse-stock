package com.nse.service;

import com.dao.StockEntity;

import java.util.List;

public interface WeeklyStockService {

    public void createWeeklyStocks(String stockSymbol) throws RuntimeException;
}
