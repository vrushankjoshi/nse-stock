package com.nse.config;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

public class MyMongoConfiguration extends AbstractMongoClientConfiguration {

    @Override
    public String getDatabaseName() {
        return "daily_stock";
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new com.nse.converter.DateReadConverter());
        adapter.registerConverter(new com.nse.converter.DateWriterConverter());
    }
}
