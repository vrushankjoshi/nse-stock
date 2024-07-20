package com.nse.utils;

import com.google.common.collect.Sets;
import com.pojo.Stock;
import com.pojo.StockResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class NSEUtils {

    public static List removeDuplicates(List inputList) {

        List<Stock> newList = new ArrayList<>(inputList);
        Set<Stock> set = Sets.newHashSet();
        CollectionUtils.addAll(newList, set);
        newList.clear();
        ;
        CollectionUtils.addAll(set, newList);

        return newList;
    }

    public static List copyList(List source, List destination) {
        if (source != null) {
            StockResponse toBean = null;
            for (Object fromBean : source) {
                if (fromBean != null) {
                    toBean = new StockResponse();
                    org.springframework.beans.BeanUtils.copyProperties(fromBean, toBean);
                    destination.add(toBean);
                }
            }
        }

        return destination;
    }

    public static long getNonWeekendDayCount(Date start, Date end) {


        LocalDate fromLocal = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocal = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long weekendDaysInt = fromLocal.datesUntil(toLocal).map(LocalDate::getDayOfWeek).filter(weekendDay -> Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(weekendDay)).count();
        return weekendDaysInt;
    }

    public static java.util.Date removeTimeFromUtilDate(java.util.Date now) throws ParseException {

        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        now = newFormat.parse(newFormat.format(now));
        return now;
    }


    public static java.util.Date localToUtilDate(LocalDate now) throws ParseException {

        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        java.util.Date nowUtilDt = java.util.Date.from(now.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        nowUtilDt = newFormat.parse(newFormat.format(nowUtilDt));


        return nowUtilDt;
    }

    public static LocalDate utilToLocalDate(java.util.Date dateToConvert) throws ParseException {

        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

        LocalDate nowUtilDt = dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return nowUtilDt;
    }

}
