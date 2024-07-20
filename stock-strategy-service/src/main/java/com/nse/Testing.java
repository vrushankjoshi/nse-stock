package com.nse;

import com.pojo.Stock;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Testing {

    public static void main(String args[]) throws ParseException {

        NavigableMap<Date, String> map = new TreeMap<Date, String>();


        LocalDate now = LocalDate.now();

        TemporalField fieldUS = WeekFields.ISO.dayOfWeek();
        System.out.println(now.with(fieldUS, 1)); // 2015-02-08 (Sunday)

        String str1 = "2024-01-31";
        java.util.Date endDate = Date.valueOf(str1);//converting string into sql date

        String str2 = "2024-01-30";
        java.util.Date startDate = Date.valueOf(str2);//converting string into sql date
        java.util.Date today = new java.util.Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        ArrayList weekList = new ArrayList();
        Date tempStartWeek = null;
        Date tempEndWeek = null;
        Stock stock = null;
        while (true) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            System.out.println("End of this week:       " + cal.getTime());
            tempEndWeek = new Date(cal.getTime().getTime());

            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            System.out.println("Start of this week:       " + cal.getTime());
            tempStartWeek = new Date(cal.getTime().getTime());
            stock = new Stock();

            stock.setWeekStartDate(tempStartWeek);
            stock.setWeekEndDate(tempEndWeek);


            //==================

            today = cal.getTime();
            today.setTime(today.getTime() - 86400000);
            cal.setTime(today);

            if (today.before(startDate))
                break;
        }

        /*

        db.stock_processing_day.update({},
        [
          {
            $set: {
                processingDay: '2024-06-03T00:00:00.000Z'

            }
          }
        ])


        db.stock_processing_day.updateMany(
        {processingDay: "2024-06-04T00:00:00.000Z"},
            { $set: { processingDay: "2024-06-03T00:00:00.000Z" } }
        )
         */


    }
}
