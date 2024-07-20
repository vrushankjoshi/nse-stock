package com.kafka.consumer;

import com.dao.ProcessingDay;
import com.kafka.producer.KafkaProducer;
import com.nse.service.DailyCalculationService;
import com.nse.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class StockConsumerListener {
    private static final Logger logger = LogManager.getLogger(StockConsumerListener.class);


    @Value(value = "${initial.records.fetch}")
    private int initialFetchRecords;
    @Autowired
    StockService stockService;

    @Autowired
    DailyCalculationService calcService;

    @Autowired
    KafkaProducer producer;

    @KafkaListener(topics = "stock-names", containerFactory = "kafkaListenerContainerFactory")
    public void newProductListener(ProcessingDay day) {
        logger.info("[StockConsumerListener] - Get request from stock-names topic " + day.getName());
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date today = new Date();
        String todayStr = lFormatter.format(today);
        java.util.Date from = null;
        String fromStr = null;
        String toStr = null;
        logger.info("[StockConsumerListener] - symbol -->"+day.getName()  + "<-- , last processingDay -->" + day.getProcessingDay()+"<--");
        if (day.getProcessingDay() == null) {
            from = DateUtils.addDays(today,initialFetchRecords*-1);
        } else {
            String dayStr = lFormatter.format(day.getProcessingDay());
            if (dayStr.equalsIgnoreCase(fromStr) || dayStr == fromStr) {
                return;
            }
            from = day.getProcessingDay();
        }
        java.util.Date to = DateUtils.addDays(today,1);

        logger.info("[StockConsumerListener] - Topic :: stock-names from -->"+from + "<-- , to -->" + to+"<--");

        try {
            //destinationList = stockService.retrieveDailyStockDtls(day.getName(), from, to);
            stockService.retrieveDailyStocks(day, from, to);

            day.setProcessingDay(DateUtils.addDays(to,-1));
            producer.send("stock-calculation",day);
        } catch (org.json.JSONException e) {
            //logger.error(e.getMessage(),e);
            logger.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "stock-calculation", containerFactory = "kafkaListenerContainerFactory")
    public void stockCalculationListener(ProcessingDay day) {
        logger.info("[StockConsumerListener] - Get request from stock-calculation topic " + day.getName() + ", date -->" + day.getProcessingDay() + "<--");
        try {
            calcService.calculateSmaEma(day.getName());
            calcService.calculateRsi(day.getName());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


    @KafkaListener(topics = "stock-calculation-from-first-day", containerFactory = "kafkaListenerContainerFactory")
    public void stockCalculationFirstDayListener(String symbol) {
        logger.info("[StockConsumerListener] - Get request from stock-calculation-from-first-day topic symbol -->" + symbol + "<--" );
        try {
            calcService.calculateSmaEmaFromDayOne(symbol);
            calcService.calculateRsiFromFirstDay(symbol);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }
}
