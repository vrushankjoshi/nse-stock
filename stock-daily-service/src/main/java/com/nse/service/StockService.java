package com.nse.service;

import com.dao.ProcessingDay;
import com.nse.constant.StockFrequency;
import com.pojo.StockResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface StockService {

    public List retrieveDailyStockDtls(String symbol, Date from, Date to);

    public List retrieveDailyStocks(ProcessingDay day ,Date from, Date to);

    public StockResponse bulishSignal(String symbol, String from, String to, int length, StockFrequency frequency);

    public List getStockNear52WeekLow(String groupId) throws IOException, ParseException;

    public List getDailyStockDetailsByGroupId(List stockList, Date from, Date to);

    public List findBullishStock(List stockList, String from, String to, int length, StockFrequency frequency);

    public java.util.Date getProcessingDayForSymbol(String symbol);

    public ProcessingDay getProcessingDayBySymbol(String symbol);
}
