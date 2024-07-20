package com.kafka.producer;

import com.dao.ProcessingDay;
import com.nse.service.StockService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;

@Slf4j
@NoArgsConstructor
@Component
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Serializable> kafkaTemplate;

    @Autowired
    StockService stockService;

    private static final Logger logger = LogManager.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, Serializable> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //public void send(String productTopic, ProductMessage message) {
    public void send(String productTopic, ProcessingDay message) {

        ProcessingDay lastProcessingDay = stockService.getProcessingDayBySymbol(message.getName());

        ListenableFuture<SendResult<String, Serializable>> future = kafkaTemplate.send(productTopic, message);


        future.addCallback(new ListenableFutureCallback<SendResult<String, Serializable>>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Topic {} : Unable to send message = {} due to: {}", productTopic, lastProcessingDay.getName(), ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Serializable> result) {
                logger.info("Topic {} : Message sent successfully with offset = {}",result.getRecordMetadata().topic(), result.getRecordMetadata().offset());
            }
        });
    }

    public void sendToAllDayCalculationTopic(String productTopic, String message) {


        ListenableFuture<SendResult<String, Serializable>> future = kafkaTemplate.send(productTopic, message);


        future.addCallback(new ListenableFutureCallback<SendResult<String, Serializable>>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Topic {} : Unable to send message = {} due to: {}", productTopic, message, ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Serializable> result) {
                logger.info("Topic {} : Message sent successfully with offset = {}",result.getRecordMetadata().topic(), result.getRecordMetadata().offset());
            }
        });
    }
}
