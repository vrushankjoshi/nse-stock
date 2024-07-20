package com.nse.service;

import com.dao.ProcessingDay;
import com.dao.StockEntity;
import com.nse.repository.StockRepositoryImpl;
import com.nse.utils.NSEUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class DailyCalculationServiceImpl implements DailyCalculationService {

    private static final Logger logger = LogManager.getLogger(DailyCalculationServiceImpl.class);

    @Value(value = "${day.rsi.length}")
    private int dayRsiLength;

    @Value(value = "${week.rsi.length}")
    private int weekRsiLength;

    @Autowired
    StockRepositoryImpl stockRepositoryImpl;

    public void calculateRsi(String symbol) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");


        // Step 1 - get processing day entity
        ProcessingDay day = stockRepositoryImpl.findProcessingDayBySymbol(symbol);
        if (day == null || day.getProcessingDay() == null) {
            logger.error("no records available for processing in Processing Day table.");
            return;
        }

        java.util.Date today = day.getProcessingDay();
        actualRsiCalculation(symbol,today);
    }

    public void calculateRsiFromFirstDay(String symbol) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");


        // Step 1 - get processing day entity
        ProcessingDay day = stockRepositoryImpl.findProcessingDayBySymbol(symbol);
        if (day == null || day.getProcessingDay() == null) {
            logger.error("no records available for processing in Processing Day table.");
            return;
        }


        java.util.Date today =  stockRepositoryImpl.getFirstRecordDate(symbol);
        actualRsiCalculation(symbol,today);
    }

    private void actualRsiCalculation(String symbol, Date today) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date processingDay = null;
        java.util.Date tomorrow = new Date();
        tomorrow =  DateUtils.addDays(tomorrow, 1);
        tomorrow = newFormat.parse(newFormat.format(tomorrow));


        StockEntity currentDayEntity = null;
        java.util.Date to = null;
        Date from = null;
        List<StockEntity> stockEntityList = null;
        float positive = 0.0F;
        float nagative = 0.0F;
        StockEntity currentDay = null;
        StockEntity prevDay = null;
        while(true) {

            if ( today.compareTo(tomorrow) >= 0 ||  today.equals(tomorrow) || today.after(tomorrow) ) {
                logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- today.after(tomorrow) -->"+ today.after(tomorrow)+ "<--");
                break;
            }
            try {
                processingDay = newFormat.parse(newFormat.format(today));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            //logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- processingDay -->" + processingDay + "<--");
            currentDayEntity = stockRepositoryImpl.findStockByKey(symbol,processingDay);
            //logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- processingDay -->" + processingDay + "<-- currentDayEntity -->" + currentDayEntity + "<--");

            if (currentDayEntity == null) {
                //logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- processingDay -->" + processingDay + "<-- currentDayEntity -->" + currentDayEntity + "<-- entity is null hence continue");
                today = DateUtils.addDays(today, 1);
                continue;
            }
            // Step 2 - get rsi length + 1 days daily stock entities from database

            to = processingDay;

            int minrequiredRecords = dayRsiLength + 1;


            try {
                from = calculateFromDate(to, minrequiredRecords);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


            stockEntityList = stockRepositoryImpl.findStockBySymbolBetweenDate(symbol,from,to);

            // Step 3 - perform RSI calculation

            if (stockEntityList.size() < dayRsiLength) {
                logger.error( symbol + " do not have required number of records("+stockEntityList.size() + " against " + minrequiredRecords + ") to calculate RSI" );
            } else {
                // calculate RSI logic
                positive = 0.0F;
                nagative = 0.0F;
                for (int i = 1 ; i < stockEntityList.size() ; i ++) {
                    prevDay = stockEntityList.get(i-1);
                    currentDay = stockEntityList.get(i);
                    if (currentDay.getChClosingPrice() > prevDay.getChClosingPrice() )
                        positive += (currentDay.getChClosingPrice() - prevDay.getChClosingPrice());
                    else
                        nagative +=(prevDay.getChClosingPrice() - currentDay.getChClosingPrice());
                }

                double rsi = calculatePreRSI(positive,nagative,dayRsiLength);
                currentDayEntity.setRsi(rsi);

                // Step 4 - update current date's stock entity into database
                stockRepositoryImpl.saveEntity(currentDayEntity);
            }

            today = DateUtils.addDays(today, 1);
        }
    }

    private Date calculateFromDate(Date to, int minrequiredRecords) throws ParseException {

        java.util.Date from = DateUtils.addDays(to,(minrequiredRecords*-1));

        long weekendDays = NSEUtils.getNonWeekendDayCount(from,to);

        if (weekendDays >0 ) {
            //logger.info("[CalculationServiceImpl] - calculateFromDate() :: 1st weekendDays -->" + weekendDays + "<--");
            from = DateUtils.addDays(from, (int)(weekendDays*-1));
            //logger.info("[CalculationServiceImpl] - calculateFromDate() :: 1st After weekendDays -->" + from + "<--");
        }


        LocalDate prevDayLocal = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        while (true) {
            if ( stockRepositoryImpl.isHoliday(prevDayLocal) || (prevDayLocal.getDayOfWeek() == DayOfWeek.SUNDAY) || (prevDayLocal.getDayOfWeek() == DayOfWeek.SATURDAY)  ) {
                prevDayLocal = prevDayLocal.minusDays(1);
            } else
                break;
        }

        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        String procDayStr =  prevDayLocal.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        from = lFormatter.parse(procDayStr);


        logger.info("[CalculationServiceImpl] - calculateFromDate() :: Final after noOfHolidaysBetweenDates from Date -->" + from + "<--");

        return from;

    }

    private double calculatePreRSI(float postive, float nagative, int rsiLength) {
        float positiveAvg = 0;
        float nagativeAvg = 0;
        double rsi;

        positiveAvg = postive / rsiLength;
        nagativeAvg = nagative / rsiLength;

        double rs = positiveAvg / nagativeAvg;
        rsi = 100 - (100 / (1 + rs));
        return rsi;
    }
    @Override
    public void calculateSmaEma(String symbol) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        ProcessingDay day = stockRepositoryImpl.findProcessingDayBySymbol(symbol);
        if (day == null || day.getProcessingDay() == null) {
            logger.error("no records available for processing in Processing Day table.");
            return;
        }

        java.util.Date today = day.getProcessingDay();

        calcMovingAverage(symbol,today, day);




    }

    public void calculateSmaEmaFromDayOne(String symbol) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        ProcessingDay day = stockRepositoryImpl.findProcessingDayBySymbol(symbol);
        if (day == null || day.getProcessingDay() == null) {
            logger.error("no records available for processing in Processing Day table.");
            return;
        }

        Date today = stockRepositoryImpl.getFirstRecordDate(symbol);

        calcMovingAverage(symbol,today, day);

    }

    public void calcMovingAverage(String symbol,Date today, ProcessingDay day) throws ParseException {



        Date processingDay = null;
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        java.util.Date from = null;
        java.util.Date tomorrow = new Date();
        tomorrow =  DateUtils.addDays(tomorrow, 1);
        tomorrow = newFormat.parse(newFormat.format(tomorrow));
        StockEntity currentDayEntity = null;
        while(today.before(tomorrow) ) {
            from = calculateFromDate(today, dayRsiLength);

            try {
                //processingDay = newFormat.parse(newFormat.format(day.getProcessingDay()));
                processingDay = newFormat.parse(newFormat.format(today));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            //logger.info("[CalculationServiceImpl] - calcMovingAverage() :: symbol -->" + symbol + ", processingday -->" + processingDay + "<--" );
            currentDayEntity = stockRepositoryImpl.findStockByKey(symbol,processingDay);

            // Step 1 - get Daily data from Data base for given symbol & Date
            logger.info("[CalculationServiceImpl] - calcMovingAverage() :: symbol -->" + symbol + "<-- , from -->" + from + "<-- today -->" + today + "<--");

            List<StockEntity> stockEntityList = stockRepositoryImpl.findStockBySymbolBetweenDate(symbol,from,today);
            if (currentDayEntity == null || ( stockEntityList == null || stockEntityList.size() < dayRsiLength )){
                today = DateUtils.addDays(today, 1);
                continue;
            }
            // Step 2 - perform calculation
            currentDayEntity = calculateMovingAverage(stockEntityList,currentDayEntity, dayRsiLength);

            // Step 3 - update data into database
            currentDayEntity = stockRepositoryImpl.saveEntity(currentDayEntity);

            today = DateUtils.addDays(today, 1);

        }
    }

    private StockEntity calculateMovingAverage(List<StockEntity> stockEntityList, StockEntity currentDayEntity, int rsiLength ) throws ParseException {
        float divider = rsiLength + 1;
        float multiplier = 2 / divider;
        DecimalFormat df = new DecimalFormat("#.##");
        StockEntity entity = null;

        float sumVal = 0;

        StockEntity prevDayStock = null;

        java.util.Date prevDay = DateUtils.addDays(currentDayEntity.getChTimestamp(), -1);

        LocalDate prevDayLocal = prevDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //logger.info("[CalculationServiceImpl] - calculateMovingAverage() :: symbol -->" +currentDayEntity.getChSymbol() + "<-- : currentDate -->" + currentDayEntity.getChTimestamp() + "prevDayLocal -->" + prevDayLocal + "<--");



        while (true) {
            if ( stockRepositoryImpl.isHoliday(prevDayLocal) || (prevDayLocal.getDayOfWeek() == DayOfWeek.SUNDAY) || (prevDayLocal.getDayOfWeek() == DayOfWeek.SATURDAY)  ) {
                prevDayLocal = prevDayLocal.minusDays(1);
            } else
                break;
        }
        //logger.info("[CalculationServiceImpl] - calculateMovingAverage() :: symbol -->" +currentDayEntity.getChSymbol() + "<-- : currentDate -->" + currentDayEntity.getChTimestamp() + "prevDayLocal(After Change) -->" + prevDayLocal + "<--");


        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        String procDayStr =  prevDayLocal.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        prevDay = lFormatter.parse(procDayStr);

        //logger.info("[CalculationServiceImpl] - calculateMovingAverage() :: symbol -->" +currentDayEntity.getChSymbol() + "<-- : currentDate -->" + currentDayEntity.getChTimestamp() + "prevDay(After Change) -->" + prevDay + "<--");


        for (int i = 0 ; i < stockEntityList.size() ; i++) {
            entity = stockEntityList.get(i);
            //logger.info("[CalculationServiceImpl] - calculateMovingAverage() :: symbol -->" +currentDayEntity.getChSymbol() + "<-- : prevDay -->" + prevDay + "<-- : entity.getChTimestamp() -->" + entity.getChTimestamp() + "<--");
            if (entity.getChTimestamp().compareTo(prevDay) == 0 )
                prevDayStock = entity;

            sumVal += entity.getChClosingPrice();
        }

        int lengthForRsiCalculation = Math.min(stockEntityList.size(), rsiLength);
        if (stockEntityList.size() <= rsiLength) {
            logger.info("symbol -->" + currentDayEntity.getChSymbol() + "<-- : current Date -->" + currentDayEntity.getChTimestamp() + "<-- : rsiLength -->" + rsiLength + "<-- : stockEntityList.size -->" + stockEntityList.size() + "<--");
        }

        currentDayEntity.setSma( Float.parseFloat(df.format( sumVal / lengthForRsiCalculation ) ) );
        if (stockEntityList.size() <= rsiLength)
            currentDayEntity.setEma( Float.parseFloat(df.format( sumVal / lengthForRsiCalculation ) ) );
        else {
            sumVal = (currentDayEntity.getChClosingPrice() * multiplier) + (prevDayStock.getEma() * (1 - multiplier));
            currentDayEntity.setEma(Float.parseFloat(df.format(sumVal)));
        }

        return currentDayEntity;

    }


}
