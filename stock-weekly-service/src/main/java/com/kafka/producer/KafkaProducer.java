package com.kafka.producer;

import com.nse.service.CalculateWeeklyService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.ParseException;

@Slf4j
@NoArgsConstructor
@Component
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Serializable> kafkaTemplate;

    @Autowired
    CalculateWeeklyService calculationService;


    private static final Logger logger = LogManager.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, Serializable> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void send(String productTopic, String message) {

        logger.info("kafka weekly calculation service - message received -->" + message + "<--");

        kafkaTemplate.send(productTopic, message);
        // call service here.
        try {
            calculationService.performCalculation();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
