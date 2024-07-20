package com.nse.repository;


import com.dao.*;
import com.mongodb.client.result.DeleteResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Repository
public class WeeklyStockRepositoryImpl {

    private static final Logger logger = LogManager.getLogger(WeeklyStockRepositoryImpl.class);



    @Autowired
    private MongoTemplate mongoTemplate;

    public List saveAllEntities(List<WeeklyStockEntity> entities) {
        List responseSaved = Arrays.asList(mongoTemplate.insertAll(entities).toArray());
        return responseSaved;

    }

    public WeeklyStockEntity saveEntity(WeeklyStockEntity entity) {
        deleteStockBySymbolAndDate(entity.getChSymbol(),entity.getWeekStartDate());

        entity = mongoTemplate.save(entity);
        return entity;

    }

    public void deleteStockBySymbolAndDate(String symbol, Date from) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
                        .andOperator(
                                Criteria.where("weekStartDate").is(from)
                        )
        );


        DeleteResult result = mongoTemplate.remove(query, WeeklyStockEntity.class);
        logger.info("[WeeklyStockRepositoryImpl] - deleteStockBySymbolAndDate() :: deleted Count symbol -->" + symbol + "<-- date from -->" + from +
                "<-- deletedCount -->"+result.getDeletedCount() + "<--");
    }

    public List<WeeklyStockEntity> findStockBySymbolBetweenDate(String symbol, Date from, Date to) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
                        .andOperator(
                                Criteria.where("weekStartDate").gte(from),
                                Criteria.where("weekStartDate").lte(to)
                        )
        ).with(Sort.by(Sort.Order.asc("weekStartDate")));


        List<WeeklyStockEntity> lookupObjects = mongoTemplate.find(query, WeeklyStockEntity.class);

        return lookupObjects;

    }

    public WeeklyStockEntity findWeeklyStockBySymbolAndStartDate(String symbol, Date day) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
                        .andOperator(
                                Criteria.where("weekStartDate").is(day)
                        )
        );


        WeeklyStockEntity stockEntity = mongoTemplate.findOne(query, WeeklyStockEntity.class);

        return stockEntity;

    }

    public StockEntity findFirstDailyRecord(String symbol) {
        Query query = new Query(Criteria.where("chSymbol").is(symbol))
                .with(
                        Sort.by(Sort.Order.asc("weekStartDate"))
                ).limit(1);

        StockEntity stockEntity = mongoTemplate.findOne(query, StockEntity.class);

        return stockEntity;

    }


    public WeeklyStockEntity findFirstWeeklyRecord(String symbol) {
        Query query = new Query(Criteria.where("chSymbol").is(symbol))
                .with(
                        Sort.by(Sort.Order.asc("weekStartDate"))
                ).limit(1);

        WeeklyStockEntity stockEntity = mongoTemplate.findOne(query, WeeklyStockEntity.class);

        return stockEntity;

    }


    public WeeklyStockEntity findLastWeeklyRecord(String symbol) {
        Query query = new Query(Criteria.where("chSymbol").is(symbol))
                .with(
                        Sort.by(Sort.Order.desc("weekStartDate"))
                ).limit(1);

        WeeklyStockEntity stockEntity = mongoTemplate.findOne(query, WeeklyStockEntity.class);

        return stockEntity;

    }




}