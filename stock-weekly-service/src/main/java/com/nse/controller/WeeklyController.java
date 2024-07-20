package com.nse.controller;


import com.dao.ProcessingDay;
import com.kafka.producer.KafkaProducer;
import com.nse.service.CalculateWeeklyService;
import com.nse.service.WeeklyStockService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/")
public class WeeklyController {

    private static final Logger logger = LogManager.getLogger(WeeklyController.class);


    @Autowired
    WeeklyStockService weeklyService;

    @Autowired
    CalculateWeeklyService calcService;

    @Autowired
    KafkaProducer producer;

    public WeeklyController() {

    }

    @GetMapping("/prepare/weekly/{symbol}")
    @ResponseBody
    public ResponseEntity<Object> createWeeklyData(@PathVariable String symbol){

        ResponseEntity rEntity = null;
        boolean success = true;
        try {
            logger.info("[WeeklyController] - createWeeklyData() : symbol -->" + symbol +"<--");
            if (symbol == null || symbol.trim().toLowerCase().equalsIgnoreCase("all"))
                weeklyService.buildWeeklyStockData();
            else
                weeklyService.createWeeklyStocks(symbol);
        } catch ( RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().equalsIgnoreCase("-1"))
                rEntity= ResponseEntity.status(HttpStatus.NOT_FOUND).body("No daily Data Found");
            success = false;
        } catch (ParseException e) {
            e.printStackTrace();
            success = false;
        }

        if(success)
            rEntity = ResponseEntity.status(HttpStatus.OK).body("weekly data created");

        producer.send("stock-weekly-calculation","process weekly calculation");


        return rEntity;

    }


    @GetMapping("/prepare/weekly/calculation/{symbol}")
    @ResponseBody
    public ResponseEntity<Object> createWeeklySmaEmaData(@PathVariable String symbol){

        ResponseEntity rEntity = null;
        boolean success = true;
        try {
            logger.info("[WeeklyController] - createWeeklySmaEmaData() : symbol -->" + symbol +"<--");

            if (symbol == null || symbol.trim().toLowerCase().equalsIgnoreCase("all"))
                calcService.performCalculationFromScratch();
            else {
                calcService.calculateSmaEmaFromDayOne(symbol);
                ProcessingDay day = new ProcessingDay();
                day.setName(symbol);
                calcService.calculateRsi(day);
            }
        } catch ( RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().equalsIgnoreCase("-1"))
                rEntity= ResponseEntity.status(HttpStatus.NOT_FOUND).body("No daily Data Found");
            success = false;
        } catch (ParseException e) {
            e.printStackTrace();
            success = false;
        }

        if(success)
            rEntity = ResponseEntity.status(HttpStatus.OK).body("weekly data created");

        return rEntity;

    }









}