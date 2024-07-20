package com.nse.service;

import java.text.ParseException;

public interface DailyCalculationService {

    public void calculateSmaEma(String symbol)throws ParseException;

    public void calculateSmaEmaFromDayOne(String symbol) throws ParseException;
    public void calculateRsi(String symbol) throws ParseException;
    public void calculateRsiFromFirstDay(String symbol) throws ParseException;
}
