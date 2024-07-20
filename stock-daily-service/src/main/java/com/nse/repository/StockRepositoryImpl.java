package com.nse.repository;


import com.dao.*;
import org.apache.commons.lang3.time.DateUtils;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    public Date getFirstRecordDate(String symbol) {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        Query query = new Query(
                Criteria.where("chSymbol").is(symbol)
        ).with(Sort.by(Sort.Order.asc("chTimestamp")));;


        StockEntity stockEntity = mongoTemplate.findOne(query, StockEntity.class);

        return stockEntity.getChTimestamp();

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

    public boolean isHoliday(LocalDate inDate) throws ParseException {
        //logger.info("===================================================");
        Query query = new Query();

        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

        String procDay = null;

        procDay =  inDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Date checkDate = lFormatter.parse(procDay);


        query.addCriteria(Criteria.where("holiday_date").is(checkDate));

        HolidayListEntity entity = mongoTemplate.findOne(query, HolidayListEntity.class);
        boolean flag = false;
        if (entity != null)
            flag = true;

        logger.info("indate -->" + inDate + "<-- : flag -->" + flag + "<--");

        //logger.info("************************=================*********************");

        return flag;

    }

    public ConfigParameters getParameterById(String parameter) {
        ConfigParameters returnParameter = mongoTemplate.findById(parameter, ConfigParameters.class);
        return returnParameter;

    }
}