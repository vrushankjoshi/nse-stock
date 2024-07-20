package com.nse.service;

import com.dao.ProcessingDay;
import com.dao.StockEntity;
import com.dao.StockPrimaryKey;
import com.dao.WeeklyStockEntity;
import com.nse.connect.NSERetrieveAllEquityNames;
import com.nse.connect.NSERetrieveEquityDetails;
import com.nse.constant.StockFrequency;
import com.nse.repository.StockRepositoryImpl;
import com.nse.strategy.BulishStockStrategy;
import com.nse.utils.NSEUtils;
import com.pojo.ErrorResponse;
import com.pojo.Stock;
import com.pojo.StockResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StrategyServiceImpl implements StrategyService {

    private static final Logger logger = LogManager.getLogger(StrategyServiceImpl.class);

   /* @Autowired
    private StockRepository stockRepository;*/

    @Autowired
    StockRepositoryImpl stockRepositoryImpl;

    public java.util.Date getProcessingDayForSymbol(String symbol) {
        ProcessingDay lastProcessingDay = stockRepositoryImpl.findProcessingDayBySymbol(symbol);

        return lastProcessingDay.getProcessingDay();
    }

    public ProcessingDay getProcessingDayBySymbol(String symbol) {
        ProcessingDay lastProcessingDay = stockRepositoryImpl.findProcessingDayBySymbol(symbol);

        return lastProcessingDay;
    }

    public List<String> findAllStockNames() {
        List<String> names = stockRepositoryImpl.findAllStockNames();

        return names;
    }

    public List findBullishStock(List stockList, int rsiLength, StockFrequency frequency) {
        BulishStockStrategy bullish = new BulishStockStrategy();
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        StockEntity stock = null;
        List sourceList = new ArrayList();
        List list = null;
        String symbol = null;
        switch (frequency) {
            case DAILY:
                StockEntity entity = null;
                for (int i = 0; i < stockList.size(); i++) {
                    symbol = (String) stockList.get(i);
                    logger.info("i -->" + i + ", symbol -->" + symbol);
                    list = stockRepositoryImpl.findLastNDailyStockRecords(symbol,rsiLength+1);

                    StockEntity[] dailyResponse = Arrays.copyOf(list.toArray(), list.toArray().length, StockEntity[].class);

                    entity = findDailyHgherRsiLowerPrice(symbol, dailyResponse,rsiLength);

                    if (entity != null) {
                        sourceList.add(entity);
                        logger.info("i -->" + i + ", Bullish -->" + symbol + " , Date -->" +
                                entity.getChTimestamp() + ", current Price -->" + entity.getChClosingPrice() +
                                ", rsi -->" + entity.getRsi() + ", 52Week -->" + entity.getCh52WeekHighPrice());
                    }

                }
                break;
            case WEEKLY:
                WeeklyStockEntity weeklyEentity = null;
                for (int i = 0; i < stockList.size(); i++) {
                    symbol = (String) stockList.get(i);
                    logger.info("i -->" + i + ", symbol -->" + symbol);
                    list = stockRepositoryImpl.findLastNWeeklyStockRecords(symbol,rsiLength+1);

                    logger.info("i -->" + i + ", symbol -->" + symbol + ", list.size -->" + list.size() + "<--");
                    WeeklyStockEntity[] weeklyResponse = Arrays.copyOf(list.toArray(), list.toArray().length, WeeklyStockEntity[].class);
                    weeklyEentity = findWeeklyHgherRsiLowerPrice(symbol, weeklyResponse,rsiLength);

                    if (weeklyEentity != null) {
                        sourceList.add(weeklyEentity);
                        logger.info("i -->" + i + ", Bullish -->" + symbol + " , Week Start Date -->" +
                                weeklyEentity.getWeekStartDate() + ", current Price -->" + weeklyEentity.getChClosingPrice() +
                                ", rsi -->" + weeklyEentity.getRsi() + ", 52Week -->" + weeklyEentity.getCh52WeekHighPrice());
                    }

                }
                break;
        }

        List<StockResponse> destinationList = new ArrayList<StockResponse>();
        destinationList = NSEUtils.copyList(sourceList, destinationList);
        return destinationList;

    }


    private StockEntity findDailyHgherRsiLowerPrice(String symbol, StockEntity[] response, int lastNoOfDays) {

        StockEntity prevStock = null;
        StockEntity currStock = null;
        boolean successFlag = false;
        int startPoint = 0;
        logger.info("symbol -->" + symbol + ", response.length -->" + response.length + "<-- , lastNoOfDays -->" + lastNoOfDays + "<--" );
        if (response.length > lastNoOfDays) {
            successFlag = true;
            startPoint = response.length - lastNoOfDays;
            //for (int i = response.length - 1; i > startPoint; i--) {
            for (int i = startPoint; i < response.length; i++) {
                prevStock = response[i - 1];
                currStock = response[i];
                logger.info("symbol -->" + symbol + "<--, prevStock.getChTimestamp -->"+ prevStock.getChTimestamp() + "<-- prevStock.getChClosingPrice() -->"+prevStock.getChClosingPrice()+"<--"+
                        "<-- , prevStock.getRsi() -->" + prevStock.getRsi() + "<--, currStock.getChTimestamp -->"+currStock.getChTimestamp()+"<--, currStock.getChClosingPrice() -->" +
                        currStock.getChClosingPrice() + "<-- currStock.getRsi() -->" + currStock.getRsi() + "<--");

                if (prevStock.getChClosingPrice() > currStock.getChClosingPrice() ||
                        prevStock.getRsi() < currStock.getRsi()) {
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
            currStock = response[0];
        else
            currStock = null;


        return currStock;
    }

    private WeeklyStockEntity findWeeklyHgherRsiLowerPrice(String symbol, WeeklyStockEntity[] response, int lastNoOfDays) {

        WeeklyStockEntity prevStock = null;
        WeeklyStockEntity currStock = null;
        boolean successFlag = false;
        int startPoint = 0;
        logger.info("symbol -->" + symbol + ", response.length -->" + response.length + "<-- , lastNoOfDays -->" + lastNoOfDays + "<--" );
        if (response.length > lastNoOfDays) {
            successFlag = true;
            startPoint = response.length - lastNoOfDays;
            //for (int i = response.length - 1; i > startPoint; i--) {
            for (int i = startPoint; i < response.length; i++) {
                prevStock = response[i - 1];
                currStock = response[i];
                logger.info("symbol -->" + symbol + "<--, prevStock.weekStartDate -->"+ prevStock.getWeekStartDate() + "<-- prevStock.getChClosingPrice() -->"+prevStock.getChClosingPrice()+"<--"+
                        "<-- , prevStock.getRsi() -->" + prevStock.getRsi() + "<--, currStock.weekStartDate -->"+currStock.getWeekStartDate()+"<--, currStock.getChClosingPrice() -->" +
                        currStock.getChClosingPrice() + "<-- currStock.getRsi() -->" + currStock.getRsi() + "<--");
                if (prevStock.getChClosingPrice() > currStock.getChClosingPrice() ||
                        prevStock.getRsi() < currStock.getRsi()) {
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
            currStock = response[0];
        else
            currStock = null;


        return currStock;
    }

    public List<StockResponse> getStockNear52WeekLow(String groupId) throws IOException {
        List<StockResponse> responseList = new ArrayList<StockResponse>();
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();

        List<String> stockList = names.getStockNames(URLEncoder.encode(groupId));

        if (stockList != null) {
            stockList.remove(groupId);
        }
        List<ProcessingDay> stockEntities = stockRepositoryImpl.findAllStockProcessingDay();

        if (stockEntities != null & stockEntities.size() > 0) {
            StockEntity stock = null;
            for ( int i = 0 ; i < stockEntities.size() ; i ++) {
                ProcessingDay day = (ProcessingDay) stockEntities.get(i);

                stock = (StockEntity) stockRepositoryImpl.findStockByKey( day.getName(),day.getProcessingDay());
                if (stock != null ) {
                    float varianceFrom52WeekLow = stock.getCh52WeekLowPrice() - stock.getChClosingPrice();
                    varianceFrom52WeekLow = (varianceFrom52WeekLow / stock.getCh52WeekLowPrice()) * 100;
                    logger.info(i + " symbol -->" + stock.getChSymbol() + "<-- varianceFrom52WeekLow -->" +
                            varianceFrom52WeekLow + "<--");
                    if (varianceFrom52WeekLow > -20 && varianceFrom52WeekLow < 20) {
                        logger.info(i + " symbol -->" + stock.getChSymbol() + " near to low 52week Low price -->" +
                                stock.getCh52WeekLowPrice() +
                                "<-- closing price -->" + stock.getChClosingPrice());

                        responseList.add(copyFromDAOResponseObject(stock));
                    }
                }
            }
        }

        return responseList;
    }

    private StockResponse copyFromDAOResponseObject(StockEntity source) {
        StockResponse destination = null;
        if (source != null) {
            destination = new StockResponse();
            destination.setChSymbol(source.getChSymbol());
            destination.setChTimestamp(source.getChTimestamp());
            destination.setWeekStartDate(source.getWeekStartDate());
            destination.setWeekEndDate(source.getWeekEndDate());
            destination.setEma(source.getEma());
            destination.setSma(source.getSma());
            destination.setRsi(source.getRsi());
            destination.setChClosingPrice(source.getChClosingPrice());
            destination.setCh52WeekHighPrice(source.getCh52WeekHighPrice());
            destination.setCh52WeekLowPrice(source.getCh52WeekLowPrice());

        }

        return destination;
    }



}
