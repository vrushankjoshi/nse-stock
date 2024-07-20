package com.kafka.consumer;

import com.kafka.producer.KafkaProducer;
import com.nse.service.CalculateWeeklyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Slf4j
@Service
public class StockConsumerListener {
    private static final Logger logger = LogManager.getLogger(StockConsumerListener.class);

    @Value(value = "${day.rsi.length}")
    private int dayRsiLength;

    @Value(value = "${week.rsi.length}")
    private int weekRsiLength;


    @Autowired
    CalculateWeeklyService calcService;

    @Autowired
    KafkaProducer producer;


    @KafkaListener(topics = "stock-weekly-calculation", containerFactory = "kafkaListenerContainerFactory")
    public void stockCalculationListener(String message) {
        logger.info("[StockConsumerListener] - Get request from stock-calculation topic message -->" + message + "<--");
        try {
            calcService.performCalculation();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
