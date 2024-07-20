package com.nse.repository;


import com.dao.*;
import com.mongodb.client.FindIterable;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jni.Proc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class StockRepositoryImpl {

    private static final Logger logger = LogManager.getLogger(StockRepositoryImpl.class);



    @Autowired
    private MongoTemplate mongoTemplate;

    public List<StockEntity> findStockBySymbolBetweenDate(String symbol, Date from, Date to) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
                        .andOperator(
                                Criteria.where("chTimestamp").gte(from),
                                Criteria.where("chTimestamp").lte(to)
                        )
        ).with(Sort.by(Sort.Order.asc("chTimestamp")));


        List<StockEntity> lookupObjects = mongoTemplate.find(query, StockEntity.class);

        return lookupObjects;

    }

    public StockEntity findStockByKey(String symbol, Date day) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
                        .andOperator(
                                Criteria.where("chTimestamp").is(day)
                        )
        );


        StockEntity stockEntity = mongoTemplate.findOne(query, StockEntity.class);

        return stockEntity;

    }

    public List<StockEntity> findLastNDailyStockRecords(String symbol, int rsiLength) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol) )
                .with(Sort.by(Sort.Order.desc("chTimestamp")))
                .limit(rsiLength);


        List<StockEntity> stockEntity = mongoTemplate.find(query, StockEntity.class);

        return stockEntity;

    }

    public List<WeeklyStockEntity> findLastNWeeklyStockRecords(String symbol, int rsiLength) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol) )
                .with(Sort.by(Sort.Order.desc("weekStartDate")))
                .limit(rsiLength);


        List<WeeklyStockEntity> stockEntity = mongoTemplate.find(query, WeeklyStockEntity.class);

        return stockEntity;

    }

    public List<ProcessingDay> findAllStockProcessingDay() {
        List<ProcessingDay> stockEntityList = mongoTemplate.findAll(ProcessingDay.class);
        return stockEntityList;

    }

    public List<String> findAllStockNames() {

        List<ProcessingDay> stockEntityList = mongoTemplate.findAll(ProcessingDay.class);

        List values  = new ArrayList<String>();
        ProcessingDay day = null;
        for (int i = 0 ; i < stockEntityList.size();i++) {
            day = stockEntityList.get(i);
            values.add(day.getName());
        }
        return values;

    }



    public List deleteStockBySymbolAndDate(String symbol, Date from, Date to) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
                        .andOperator(
                                Criteria.where("chTimestamp").gte(from),
                                Criteria.where("chTimestamp").lte(to)
                        )
        );


        List deletedObjects = mongoTemplate.findAllAndRemove(query, StockEntity.class);

        return deletedObjects;

    }

    public ProcessingDay saveProcessingDayEntity(ProcessingDay entity) {
        entity = mongoTemplate.save(entity);
        return entity;

    }

    public ProcessingDay findProcessingDayBySymbolAndDate(String symbol, Date day) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("symbol").is(symbol)
                        .andOperator(
                                Criteria.where("processingDay").is(day)
                        )
        );


        ProcessingDay procssingDay = mongoTemplate.findOne(query, ProcessingDay.class);

        return procssingDay;

    }

    public StockEntity saveEntity(StockEntity entity) {
        entity = mongoTemplate.save(entity);
        return entity;

    }

    public List saveAllEntities(List entities) {
        List responseSaved = Arrays.asList(mongoTemplate.insertAll(entities).toArray());
        return responseSaved;

    }

    public StockEntity findById(StockPrimaryKey key) {

        StockEntity entity = mongoTemplate.findById(key, StockEntity.class);
        return entity;

    }

    public ProcessingDay findProcessingDayBySymbol(String symbol) {
        ProcessingDay processingDay = mongoTemplate.findById(symbol, ProcessingDay.class);

        return processingDay;
    }

    public ProcessingDay insertUpdateProcessingDayForSymbol(String symbol, Date processingDate) {
        ProcessingDay day = findProcessingDayBySymbol(symbol);
        if ( day == null ) {
            day = new ProcessingDay();
            day.setName(symbol);
        }
        day.setProcessingDay(processingDate);
        day.setUpdatedDate(new Date());
        day = mongoTemplate.save(day);
        return day;

    }

    public int getHolidayCount(Date from, Date to) throws ParseException {
        Query query = new Query();

        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

        String procDay = null;
        procDay = lFormatter.format(from );
        from = lFormatter.parse(procDay);

        procDay = lFormatter.format(to );
        to = lFormatter.parse(procDay);

        query.addCriteria(Criteria.where("holiday_date").gte(from).lte(to));

        List numberOfRecords = mongoTemplate.find(query, HolidayListEntity.class);

        int holidayDays = numberOfRecords == null ? 0 : numberOfRecords.size();

        return holidayDays;

    }

    public ConfigParameters getParameterById(String parameter) {
        ConfigParameters returnParameter = mongoTemplate.findById(parameter, ConfigParameters.class);
        return returnParameter;

    }
}