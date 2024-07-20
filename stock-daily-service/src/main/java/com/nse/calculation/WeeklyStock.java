package com.nse.calculation;

import com.pojo.Stock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class WeeklyStock {
    private static final Logger logger = LogManager.getLogger(WeeklyStock.class);

    private Stock[] stockArray = null;

    private Date findStartDate = null;

    private Date findEndDate = null;

    private String stockSymbol = null;

    private HashMap dailyStockDtls = null;

    public WeeklyStock(Date findStartDate, Date findEndDate, String stockSymbol, Stock[] stockArray) {
        this.stockArray = stockArray;
        this.findStartDate = findStartDate;
        this.findEndDate = findEndDate;
        this.stockSymbol = stockSymbol;
        dailyStockDtls = new HashMap();
        for (int i = 0; i < stockArray.length; i++) {
            dailyStockDtls.put(stockArray[i].getChTimestamp(), stockArray[i]);
        }
    }

    public Date getFindStartDate() {
        return findStartDate;
    }

    public void setFindStartDate(Date findStartDate) {
        this.findStartDate = findStartDate;
    }

    public Date getFindEndDate() {
        return findEndDate;
    }

    public void setFindEndDate(Date findEndDate) {
        this.findEndDate = findEndDate;
    }

    public HashMap buildWeeklyStockDetails() {

        //logger.info("============ Prepare Weekly Stock Details =========================");
        HashMap<Long, Stock> weeklyStockDtls = new HashMap();


        Date tempStartWeek = null;
        Date tempEndWeek = null;
        Stock weeklyStock = null;
        java.util.Date today = null;
        Set dateSet = dailyStockDtls.keySet();
        Iterator dateItr = dateSet.iterator();
        //logger.info("dateSet.size() -->"+dateSet.size());


        LocalDate endLocalDt = findEndDate.toLocalDate();
        while (endLocalDt.getDayOfWeek() != DayOfWeek.SUNDAY) {
            endLocalDt = endLocalDt.plusDays(1);
        }

        Date stockDate = null;
        Stock tempStock = null;
        long dateTime = 0;
        while (true) {

            tempEndWeek = Date.valueOf(endLocalDt);
            tempStartWeek = Date.valueOf(endLocalDt.minusDays(6));

            weeklyStock = new Stock();

            //logger.info("========== Start -->" + tempStartWeek + "<-- END -->" + tempEndWeek +"<-- ===============");

            weeklyStock.setChSymbol(stockSymbol);

            dateItr = dateSet.iterator();
            dateTime = 0;
            boolean dataFound = false;
            while (dateItr.hasNext()) {
                stockDate = (Date) dateItr.next();
                if ((stockDate.before(tempEndWeek) && stockDate.after(tempStartWeek)) ||
                        stockDate.getTime() == tempStartWeek.getTime() ||
                        stockDate.getTime() == tempEndWeek.getTime()) {
                    dataFound = true;
                    tempStock = (Stock) dailyStockDtls.get(stockDate);
                    //System.out.println("TempStockDate -->" + tempStock.getChTimestamp() + "<==");
                    weeklyStock = buildWeeklyStock(tempStock, weeklyStock);
                    dateTime = weeklyStock.getWeekStartDate().getTime();

                }
            }

            if (dataFound && weeklyStock.getChTimestamp() != null) {
                weeklyStockDtls.put(dateTime, weeklyStock);
            }


            endLocalDt = tempStartWeek.toLocalDate().minusDays(1);


            if (Date.valueOf(endLocalDt).before(findStartDate))
                break;
        }

        //logger.info("Total weeks -->" + weeklyStockDtls.values().toArray().length);

        return weeklyStockDtls;
    }


    private Stock buildWeeklyStock(Stock tempStock, Stock weeklyStock) {

        if (weeklyStock.getChTimestamp() == null) {
            weeklyStock.setChTimestamp(tempStock.getChTimestamp());
            weeklyStock.setWeekStartDate(tempStock.getChTimestamp());
            weeklyStock.setWeekEndDate(tempStock.getChTimestamp());
            weeklyStock.setChClosingPrice(tempStock.getChClosingPrice());
            weeklyStock.setChTradeHighPrice(tempStock.getChTradeHighPrice());
            weeklyStock.setChTradeLowPrice(tempStock.getChTradeLowPrice());
            weeklyStock.setChOpeningPrice(tempStock.getChOpeningPrice());
            weeklyStock.setCh52WeekHighPrice(tempStock.getCh52WeekHighPrice());
            weeklyStock.setCh52WeekLowPrice(tempStock.getCh52WeekLowPrice());

        } else if (weeklyStock.getChTimestamp() != null && weeklyStock.getChTimestamp().after(tempStock.getChTimestamp())) {
            weeklyStock.setChTimestamp(tempStock.getChTimestamp());
            weeklyStock.setWeekStartDate(tempStock.getChTimestamp());

        }

        if (weeklyStock.getWeekStartDate() != null &&
                (weeklyStock.getWeekStartDate().after(tempStock.getChTimestamp()) ||
                        weeklyStock.getWeekStartDate().equals(tempStock.getChTimestamp()))) {

            if (weeklyStock.getChOpeningPrice() > tempStock.getChOpeningPrice())
                weeklyStock.setChOpeningPrice(tempStock.getChOpeningPrice());
        }


        if (weeklyStock.getWeekEndDate() != null &&
                weeklyStock.getWeekEndDate().before(tempStock.getChTimestamp())) {

            weeklyStock.setWeekEndDate(tempStock.getChTimestamp());
            weeklyStock.setChClosingPrice(tempStock.getChClosingPrice());
        }


        if (weeklyStock.getChTradeHighPrice() < tempStock.getChTradeHighPrice())
            weeklyStock.setChTradeHighPrice(tempStock.getChTradeHighPrice());

        if (weeklyStock.getChTradeLowPrice() > tempStock.getChTradeLowPrice())
            weeklyStock.setChTradeLowPrice(tempStock.getChTradeLowPrice());

        if (weeklyStock.getCh52WeekHighPrice() < tempStock.getCh52WeekHighPrice())
            weeklyStock.setCh52WeekHighPrice(tempStock.getCh52WeekHighPrice());

        if (weeklyStock.getCh52WeekLowPrice() > tempStock.getCh52WeekLowPrice())
            weeklyStock.setCh52WeekLowPrice(tempStock.getCh52WeekLowPrice());

        weeklyStock.setChTotTradedVal(weeklyStock.getChTotTradedVal() + tempStock.getChTotTradedVal());
        weeklyStock.setChTotalTrades(weeklyStock.getChTotalTrades() + tempStock.getChTotalTrades());
        weeklyStock.setChTotTradedQty(weeklyStock.getChTotTradedQty() + tempStock.getChTotTradedQty());

        return weeklyStock;
    }
}
