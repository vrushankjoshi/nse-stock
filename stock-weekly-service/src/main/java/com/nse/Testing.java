package com.nse;

import com.pojo.Stock;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        System.out.println("now-->" + now);

        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        java.util.Date nowUtilDt = java.util.Date.from(now.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        java.util.Date lastDayForProcessing = newFormat.parse(newFormat.format(nowUtilDt));

        System.out.println("lastDayForProcessing-->" + lastDayForProcessing);


    }
}
