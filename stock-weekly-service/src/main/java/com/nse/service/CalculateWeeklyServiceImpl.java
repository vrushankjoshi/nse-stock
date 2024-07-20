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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;

@Service
public class CalculateWeeklyServiceImpl implements CalculateWeeklyService {

    private static final Logger logger = LogManager.getLogger(CalculateWeeklyServiceImpl.class);

    private static final int NO_OF_DAYS_IN_WEEK = 7;

    @Value(value = "${day.rsi.length}")
    private int dayRsiLength;

    @Value(value = "${week.rsi.length}")
    private int weekRsiLength;

    @Value(value = "${week.init.start.days}")
    private int initialStartDays;

    @Autowired
    StockRepositoryImpl stockRepositoryImpl;

    @Autowired
    WeeklyStockRepositoryImpl weeklyStockRepositoryImpl;

    public void performCalculation() throws ParseException {
        List<ProcessingDay> dayList = stockRepositoryImpl.getAllProcessingDay();

        ProcessingDay day = null;

        for (int i = 0 ; i < dayList.size();i++) {
            day = dayList.get(i);
            calculateSmaEma(day);
            calculateRsi(day);
        }

    }

    public void performCalculationFromScratch() throws ParseException {
        List<ProcessingDay> dayList = stockRepositoryImpl.getAllProcessingDay();

        ProcessingDay day = null;
        Date currProcessingDay= null;
        for (int i = 0 ; i < dayList.size();i++) {
            day = dayList.get(i);
            day.setWeekProcessingDay(null);

            calculateSmaEma(day);
            calculateRsi(day);
        }

    }

    public void calculateRsi(ProcessingDay day) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");


        // Step 1 - get processing day entity

        if (day == null) {
            logger.error("no records available for processing in Processing Day table.");
            return;
        }
        String symbol = day.getName();

    //=============================== 1. Build last processing day ===================================================//

        Date currentProcessingStartDay = null;

        Date lastDayForProcessing = weeklyStockRepositoryImpl.findLastWeeklyRecord(symbol).getWeekStartDate();
        Date processTillDate = lastDayForProcessing;

                //=============================== 2. Build first day for processing ==============================================//

        currentProcessingStartDay = day.getWeekProcessingDay();
        if (currentProcessingStartDay == null) { //If null means, till  now weekly calculation happened so far hence start from first record onwards
            currentProcessingStartDay = weeklyStockRepositoryImpl.findFirstWeeklyRecord(symbol).getWeekStartDate();

            ///////////
        }


        currentProcessingStartDay = NSEUtils.removeTimeFromUtilDate(currentProcessingStartDay);

        logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- currentProcessingStartDay -->" +
                currentProcessingStartDay + "<-- : lastDayForProcessing -->"+ lastDayForProcessing+ "<--" +
                "currentProcessingStartDay.before(lastDayForProcessing) -->" + currentProcessingStartDay.before(lastDayForProcessing) + "<--");

        if (currentProcessingStartDay.before(lastDayForProcessing)){

            //=============================== 3. Start weekly calculation ====================================================//
            WeeklyStockEntity currentWeekDayEntity = null;
            java.util.Date currentProcessingWeekEndDate = null;
            Date from = null;
            List<WeeklyStockEntity> stockEntityList = null;
            float positive = 0.0F;
            float nagative = 0.0F;
            WeeklyStockEntity currentDay = null;
            WeeklyStockEntity prevDay = null;
            while(true) {

                /*if ( currentProcessingStartDay.compareTo(lastDayForProcessing) >= 0 ||  currentProcessingStartDay.equals(lastDayForProcessing) ||
                        currentProcessingStartDay.after(lastDayForProcessing) ) {*/
                if ( currentProcessingStartDay.compareTo(lastDayForProcessing) > 0 ||
                        currentProcessingStartDay.after(lastDayForProcessing) ) {
                    logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- today.after(lastDayForProcessing) -->"
                            + processTillDate.after(lastDayForProcessing)+ "<--");
                    break;
                }
                try {
                    currentProcessingStartDay = newFormat.parse(newFormat.format(currentProcessingStartDay));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- currentProcessingStartDay -->" +
                        currentProcessingStartDay + "<--");
                currentWeekDayEntity = weeklyStockRepositoryImpl.findWeeklyStockBySymbolAndStartDate(symbol,currentProcessingStartDay);
                logger.info("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- currentProcessingStartDay -->" +
                        currentProcessingStartDay + "<-- currentWeekDayEntity -->" + currentWeekDayEntity + "<--");

                if (currentWeekDayEntity == null) {
                    logger.error("[CalculationServiceImpl] - calculateRsi() :: symbol -->" + symbol + "<-- currentProcessingStartDay -->" +
                            currentProcessingStartDay + "<-- currentWeekDayEntity -->" + currentWeekDayEntity + "<-- entity is null hence continue");
                    currentProcessingStartDay = DateUtils.addDays(currentProcessingStartDay, NO_OF_DAYS_IN_WEEK);
                    continue;
                }
                // Step 2 - get rsi length + 1 days WEEKLY stock entities from database
                int minrequiredRecords = dayRsiLength + 1;

                currentProcessingWeekEndDate = DateUtils.addDays(currentProcessingStartDay,0);



                from = DateUtils.addDays(currentProcessingStartDay,(minrequiredRecords*NO_OF_DAYS_IN_WEEK*-1));


                stockEntityList = weeklyStockRepositoryImpl.findStockBySymbolBetweenDate(symbol,from,currentProcessingWeekEndDate);

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
                    currentWeekDayEntity.setRsi(rsi);

                    // Step 4 - update current date's stock entity into database
                    weeklyStockRepositoryImpl.saveEntity(currentWeekDayEntity);
                }

                currentProcessingStartDay = DateUtils.addDays(currentProcessingStartDay, NO_OF_DAYS_IN_WEEK); // taking today from Sunday to Monday
            }
        }

    }

    private Date calculateFromDate(Date to, int minrequiredRecords) throws ParseException {

        java.util.Date from = DateUtils.addDays(to,(minrequiredRecords*NO_OF_DAYS_IN_WEEK*-1));
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

    public void calculateSmaEmaFromDayOne(String symbol) throws ParseException {

        logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEmaFromDayOne() :: #################################### " + symbol +" ################################");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date currProcessingDay= weeklyStockRepositoryImpl.findFirstWeeklyRecord(symbol).getWeekStartDate();

        java.util.Date lastMondayDate = weeklyStockRepositoryImpl.findLastWeeklyRecord(symbol).getWeekStartDate();


        int localRsiLength = 0;
        WeeklyStockEntity currentDayEntity = null;
        Date startProcessingDay = null;
        List<WeeklyStockEntity> weeklyStockList = null;
        while (true) {
            if (currProcessingDay.after(lastMondayDate))
                break;
            else if (localRsiLength < weekRsiLength) {
                localRsiLength++;
            } else {

                currentDayEntity = weeklyStockRepositoryImpl.findWeeklyStockBySymbolAndStartDate(symbol,currProcessingDay);
                if (currentDayEntity == null ) {
                    logger.error("symbol -->" + symbol + "<-- currentProcessingDay -->" + currProcessingDay + "<--");
                    throw new RuntimeException("symbol -->" + symbol + "<-- currentProcessingDay -->" + currProcessingDay + "<--");
                }

                startProcessingDay = DateUtils.addDays(currProcessingDay,-1 * NO_OF_DAYS_IN_WEEK * weekRsiLength  );
                weeklyStockList = weeklyStockRepositoryImpl.findStockBySymbolBetweenDate(symbol,startProcessingDay,currProcessingDay);

                currentDayEntity =  calculateMovingAverage(weeklyStockList,currentDayEntity, weekRsiLength);


                weeklyStockRepositoryImpl.saveEntity(currentDayEntity);
            }

            currProcessingDay = DateUtils.addDays(currProcessingDay,NO_OF_DAYS_IN_WEEK);
        }


        logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEmaFromDayOne() :: ################################ END ######### " + symbol +" ################################");

    }

    @Override
    public void calculateSmaEma(ProcessingDay day) throws ParseException {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");


        //if (day == null || day.getWeekProcessingDay() == null) {
        if (day == null ) {
            logger.error("no records available for processing in Processing Day table.");
            return;
        }
        String symbol = day.getName();
        java.util.Date currentProcessingEndDay = day.getWeekProcessingDay();
        logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEma() :: currentProcessingEndDay -->" + currentProcessingEndDay + "<--" );
        if (currentProcessingEndDay == null) { //If null means, till  now weekly calculation happened so far hence start from first record onwards
            WeeklyStockEntity entity = weeklyStockRepositoryImpl.findFirstWeeklyRecord(symbol);
            logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEma() :: WeeklyStockEntity.entity -->" + entity + "<--");
            currentProcessingEndDay = entity.getWeekStartDate();
            logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEma() :: currentProcessingEndDay -->" + currentProcessingEndDay + "<--");
        } 

        //=========================================== GET LAST DAY FOR PROCESSING ========================================//

        TemporalField dayOfWeek = WeekFields.ISO.dayOfWeek();
        LocalDate toLocal = NSEUtils.utilToLocalDate(currentProcessingEndDay);

        toLocal = toLocal.with(dayOfWeek, dayOfWeek.range().getMaximum());

        Date lastDayForProcessing = null;

        lastDayForProcessing = weeklyStockRepositoryImpl.findLastWeeklyRecord(symbol).getWeekStartDate();

        lastDayForProcessing = NSEUtils.removeTimeFromUtilDate(lastDayForProcessing);

        //===============================================================================================================//

        java.util.Date from = null;
        while(currentProcessingEndDay.before(lastDayForProcessing) || currentProcessingEndDay.compareTo(lastDayForProcessing) == 0 ) {
            logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEma() :: symbol -->"+ symbol + "<-- : currentProcessingEndDay(startDate) -->" +
                    currentProcessingEndDay + "<-- :: lastDayForProcessing -->" + lastDayForProcessing + "<--");

            from = calculateFromDate(currentProcessingEndDay, dayRsiLength);
            WeeklyStockEntity currentDayEntity = weeklyStockRepositoryImpl.findWeeklyStockBySymbolAndStartDate(symbol,currentProcessingEndDay);
            logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEma() ::  symbol -->" + symbol + "<--, currentProcessingEndDay -->" + currentProcessingEndDay + "<-- , currentDayEntity -->" + currentDayEntity + "<--");

            // Step 1 - get Daily data from Data base for given symbol & Date
            List<WeeklyStockEntity> stockEntityList = weeklyStockRepositoryImpl.findStockBySymbolBetweenDate(symbol,from,DateUtils.addDays(currentProcessingEndDay,-1));
            logger.info("[CalculateWeeklyServiceImpl] - calculateSmaEma() ::  stockEntityList.size() -->" + stockEntityList.size() + "<--");
            if (stockEntityList.size() > 0 && currentDayEntity != null ) {

                // Step 2 - perform calculation
                currentDayEntity = calculateMovingAverage(stockEntityList,currentDayEntity, dayRsiLength);

                // Step 3 - update data into database
                currentDayEntity = weeklyStockRepositoryImpl.saveEntity(currentDayEntity);
            }

            currentProcessingEndDay = DateUtils.addDays(currentProcessingEndDay, NO_OF_DAYS_IN_WEEK);


        }

    }

    private WeeklyStockEntity calculateMovingAverage(List<WeeklyStockEntity> stockEntityList, WeeklyStockEntity currentDayEntity, int rsiLength ) {
        float divider = rsiLength + 1;
        float multiplier = 2 / divider;
        DecimalFormat df = new DecimalFormat("#.##");
        WeeklyStockEntity entity = null;

        float sumVal = 0;

        WeeklyStockEntity prevDayStock = null;

        logger.info("[CalculateWeeklyServiceImpl] - calculateMovingAverage() :: currentDayEntity.symbol -->" + currentDayEntity.getChSymbol() + "<--, currentDayEntity.getWeekStartDate() -->" + currentDayEntity.getWeekStartDate() + "<--");

        java.util.Date prevDay = DateUtils.addDays(currentDayEntity.getWeekStartDate(), -1 * NO_OF_DAYS_IN_WEEK);

        logger.info("[CalculateWeeklyServiceImpl] - calculateMovingAverage() :: currentDayEntity.symbol -->" + currentDayEntity.getChSymbol() + "<--, prevDay -->" + prevDay + "<--");

        for (int i = 0 ; i < stockEntityList.size() ; i++) {
            entity = stockEntityList.get(i);
            if (entity.getWeekStartDate().compareTo(prevDay) == 0 ) // this is to get prev working day
                prevDayStock = entity;

            sumVal += entity.getChClosingPrice();
        }

        //int lengthForRsiCalculation = Math.min(stockEntityList.size(), rsiLength);

        logger.info("[CalculateWeeklyServiceImpl] - calculateMovingAverage() :: currentDayEntity.symbol -->" + currentDayEntity.getChSymbol() +
                "<--, stockEntityList.size() -->" + stockEntityList.size() + "<--, rsiLength -->" + rsiLength + "<--");

        int lengthForRsiCalculation =  (stockEntityList.size() > rsiLength) ? stockEntityList.size() : rsiLength;

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
