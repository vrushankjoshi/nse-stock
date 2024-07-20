package com.nse.service;

import com.dao.ProcessingDay;
import com.dao.StockEntity;
import com.dao.WeeklyStockEntity;
import com.nse.repository.StockRepositoryImpl;
import com.nse.repository.WeeklyStockRepositoryImpl;
import com.nse.utils.NSEUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WeeklyStockServiceImpl implements WeeklyStockService {

    private static final Logger logger = LogManager.getLogger(WeeklyStockServiceImpl.class);

    @Autowired
    StockRepositoryImpl stockRepositoryImpl;

    @Autowired
    WeeklyStockRepositoryImpl weeklyStockRepositoryImpl;

    @Value(value = "${week.init.start.days}")
    private int initialStartDays;

    public void buildWeeklyStockData() throws ParseException {
        List<ProcessingDay> processingDayList = stockRepositoryImpl.getAllProcessingDay();
        ProcessingDay day = null;
        for (int i = 0 ; i < processingDayList.size();i++) {
            day = (ProcessingDay)processingDayList.get(i);
            createWeeklyStocks(day.getName(),day);
        }

    }
    public void createWeeklyStocks(String stockSymbol, ProcessingDay lastProcessingDay) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date endDate = new java.util.Date();
        //endDate = DateUtils.addDays(endDate,1);
        logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: lastProcessingDay -->" + lastProcessingDay +"<--");

        //================================== 1. build overall START date =============================================//
        java.util.Date startDate = null;
        if (lastProcessingDay == null  ) {
            throw new RuntimeException("-1");
        } else {
            startDate = lastProcessingDay.getWeekProcessingDay();
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: startDate-->"+startDate+"<--");
            if (startDate == null) {
                logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: startDate is null hence creating new");
                startDate = DateUtils.addDays(endDate,initialStartDays*-1);
                logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: startDate is null hence created new from date starting today -"+initialStartDays+" no of days -->"+ startDate + "<--");
            }
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: newly created startDate -->"+ startDate + "<--");

            LocalDate startDateLocalDt = NSEUtils.utilToLocalDate(startDate);
            //LocalDate startDateLocalDt = LocalDate.parse( newFormat.format(startDate) );
            if (startDateLocalDt.getDayOfWeek() != DayOfWeek.MONDAY) {
                while (startDateLocalDt.getDayOfWeek() != DayOfWeek.MONDAY) {
                    startDateLocalDt = startDateLocalDt.plusDays(-1);
                }
            }

            startDate = NSEUtils.localToUtilDate(startDateLocalDt);
            //startDate = newFormat.parse(newFormat.format(startDateLocalDt));
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: changed startDate-->"+startDate+"<--");

        }
        startDate = newFormat.parse(newFormat.format(startDate));

        //==================================== 2. build overall END date =============================================//
        endDate = newFormat.parse(newFormat.format(endDate));
        LocalDate endDateLocalDt = NSEUtils.utilToLocalDate(endDate);
        //LocalDate endDateLocalDt = LocalDate.parse( newFormat.format(endDate) );
        if ( endDateLocalDt.getDayOfWeek() == DayOfWeek.SATURDAY) {
            endDateLocalDt = endDateLocalDt.plusDays(1);
        } else if ((endDateLocalDt.getDayOfWeek() != DayOfWeek.SATURDAY) && (endDateLocalDt.getDayOfWeek() != DayOfWeek.SUNDAY)) {
            while (endDateLocalDt.getDayOfWeek() != DayOfWeek.SUNDAY) {
                endDateLocalDt = endDateLocalDt.plusDays(-1);
            }

        }

        endDate = NSEUtils.localToUtilDate(endDateLocalDt);
        //endDate = newFormat.parse(newFormat.format(endDateLocalDt));

        logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: startDate -->" + startDate + "<-- endDate -->" + endDate +"<--");

        //================================== 3. start DATA Processing ================================================//

        if (startDate.before ( endDate ) ) {


            //startDate = DateUtils.addDays(startDate,1);

            List<StockEntity> stockList = stockRepositoryImpl.findStockBySymbolBetweenDate(stockSymbol, startDate, endDate);
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: stockList -->" + stockList + "<--");

            if (stockList == null || stockList.size() <= 0)
                throw new RuntimeException("-1");

            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: stockList.size() -->" + stockList.size() + "<--");

            List<WeeklyStockEntity> weeklyStockListResponse = buildWeeklyStockDetails(startDate, endDate, stockSymbol, stockList);
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: weeklyStockListResponse -->" + weeklyStockListResponse + "<--");

            weeklyStockRepositoryImpl.saveAllEntities(weeklyStockListResponse);
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: stockRepositoryImpl.saveAllEntities(weeklyStockListResponse) ");


            lastProcessingDay.setWeekProcessingDay(endDate);
            stockRepositoryImpl.saveProcessingDayEntity(lastProcessingDay);
            logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: stockRepositoryImpl.saveProcessingDayEntity ");
        }
    }
    public void createWeeklyStocks(String stockSymbol) throws ParseException {

        logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: entry");

        ProcessingDay lastProcessingDay = stockRepositoryImpl.findProcessingDayBySymbol(stockSymbol);
        logger.info("[WeeklyStockServiceImpl] - createWeeklyStocks() :: lastProcessingDay -->" + lastProcessingDay +"<--");
        createWeeklyStocks(stockSymbol,lastProcessingDay);
    }

    public List<WeeklyStockEntity> buildWeeklyStockDetails(Date findStartDate, Date findEndDate, String stockSymbol, List<StockEntity> stockList) {
        logger.info("[WeeklyStockServiceImpl] - buildWeeklyStockDetails() :: findStartDate -->"+findStartDate+"<-- findEndDate-->"+ findEndDate +"<-- symbol -->" + stockSymbol + "<-- stockList -->"+ stockList.size() + "<--");

        Date tempStartWeek = null;
        Date tempEndWeek = null;
        WeeklyStockEntity weeklyStock = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        LocalDate startLocalDt = LocalDate.parse( sdf.format(findStartDate) );
        /*while (startLocalDt.getDayOfWeek() != DayOfWeek.MONDAY) {
            startLocalDt = startLocalDt.plusDays(-1);
        }*/
        logger.info("[WeeklyStockServiceImpl] - buildWeeklyStockDetails() :: startLocalDt -->" + startLocalDt + "<--");

        StockEntity temp = null;
        List<WeeklyStockEntity>  responseList = new ArrayList<WeeklyStockEntity>();
        boolean dataFound = false;

        while (true) {

            tempStartWeek = Date.from(startLocalDt.atStartOfDay(ZoneId.systemDefault()).toInstant());
            tempEndWeek = DateUtils.addDays(tempStartWeek,6);

            logger.info("[WeeklyStockServiceImpl] - buildWeeklyStockDetails() :: while loop tempStartWeek -->" + tempStartWeek + "<-- tempEndWeek -->" + tempEndWeek + "<--");

            weeklyStock = new WeeklyStockEntity();

            weeklyStock.setChSymbol(stockSymbol);


            dataFound = false;

            logger.info("[WeeklyStockServiceImpl] - buildWeeklyStockDetails() :: before for loop stockList.size() -->" + stockList.size() + "<--");
            for(int i = 0 ; i <stockList.size();i++) {
                temp = stockList.get(i);
                if ( temp.getChTimestamp().compareTo(tempStartWeek) >= 0 &&
                        temp.getChTimestamp().compareTo(tempEndWeek) <= 0 ) {
                    dataFound = true;
                    weeklyStock = buildWeeklyStock(temp, weeklyStock);
                    logger.info("[WeeklyStockServiceImpl] - buildWeeklyStockDetails() :: Added -->" +temp.getChTimestamp() + "<--" );
                }
            }

            if (dataFound && weeklyStock.getWeekStartDate() != null && weeklyStock.getWeekEndDate() != null) {
                logger.info( "[WeeklyStockServiceImpl] - buildWeeklyStockDetails() :: BEFORE ADDING WeekStartDate -->" + weeklyStock.getWeekStartDate() + "<--, WeekEndDate -->" + weeklyStock.getWeekEndDate() + "<--") ;
                weeklyStock.setWeekStartDate(tempStartWeek);
                weeklyStock.setWeekEndDate(tempEndWeek);

                responseList.add(weeklyStock);
            }

            tempEndWeek = DateUtils.addDays(tempEndWeek,1);
            if (tempEndWeek.after(findEndDate))
                break;

            startLocalDt = LocalDate.parse( sdf.format(tempEndWeek) );

        }

        return responseList;
    }


    private WeeklyStockEntity buildWeeklyStock(StockEntity tempStock, WeeklyStockEntity weeklyStock) {

        if (weeklyStock.getWeekStartDate() == null) {
            weeklyStock.setWeekStartDate(tempStock.getChTimestamp());
            weeklyStock.setWeekEndDate(tempStock.getChTimestamp());
            weeklyStock.setChClosingPrice(tempStock.getChClosingPrice());

            weeklyStock.setCh52WeekHighPrice(tempStock.getCh52WeekHighPrice());
            weeklyStock.setCh52WeekLowPrice(tempStock.getCh52WeekLowPrice());

        } else if (weeklyStock.getWeekStartDate() != null && weeklyStock.getWeekStartDate().after(tempStock.getChTimestamp())) {
            weeklyStock.setWeekStartDate(tempStock.getChTimestamp());

        }

        if (weeklyStock.getWeekEndDate() != null &&
                weeklyStock.getWeekEndDate().before(tempStock.getChTimestamp())) {

            weeklyStock.setWeekEndDate(tempStock.getChTimestamp());
            weeklyStock.setChClosingPrice(tempStock.getChClosingPrice());
        }


        if (weeklyStock.getCh52WeekHighPrice() < tempStock.getCh52WeekHighPrice())
            weeklyStock.setCh52WeekHighPrice(tempStock.getCh52WeekHighPrice());

        if (weeklyStock.getCh52WeekLowPrice() > tempStock.getCh52WeekLowPrice())
            weeklyStock.setCh52WeekLowPrice(tempStock.getCh52WeekLowPrice());

        return weeklyStock;
    }
}
