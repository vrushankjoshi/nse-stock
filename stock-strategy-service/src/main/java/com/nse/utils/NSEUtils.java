package com.nse.utils;

import com.google.common.collect.Sets;
import com.pojo.Stock;
import com.pojo.StockResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

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

}
