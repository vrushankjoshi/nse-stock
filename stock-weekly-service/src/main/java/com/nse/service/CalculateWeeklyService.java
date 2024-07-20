package com.nse.service;

import com.dao.ProcessingDay;

import java.text.ParseException;

public interface CalculateWeeklyService {

    public void performCalculation() throws ParseException;
    public void calculateSmaEma(ProcessingDay day) throws ParseException;

    public void calculateRsi(ProcessingDay day) throws ParseException;

    public void calculateSmaEmaFromDayOne(String symbol) throws ParseException;

    public void performCalculationFromScratch() throws ParseException;
}
