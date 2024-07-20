package org.example;

import org.apache.commons.lang3.time.DateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        java.util.Date to = new Date();
        java.util.Date from = DateUtils.addDays(to,-14);

        LocalDate fromLocal = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocal = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        System.out.println ("from Local -->" + fromLocal + "<-- to local -->" + toLocal + "<--");
        long weekendDaysInt = fromLocal.datesUntil(toLocal).map(LocalDate::getDayOfWeek).filter(weekendDay -> Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(weekendDay)).count();
        System.out.println ("weekendDaysInt -->"+weekendDaysInt+"<--");
    }



}