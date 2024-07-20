package com.nse.service;

import com.dao.ProcessingDay;
import com.nse.constant.StockFrequency;
import com.pojo.StockResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface StrategyService {



    public List getStockNear52WeekLow(String groupId) throws IOException, ParseException;

    public List findBullishStock(List stockList, int length, StockFrequency frequency);

    public List<String> findAllStockNames();
}
