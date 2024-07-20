package com.nse.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@ComponentScan(basePackageClasses= StockStrategyController.class)
@ComponentScan(basePackages = {"com.dao", "com.nse.repository","com.kafka", "com.nse.controller",
        "com.nse.domain", "com.nse.strategy", "com.nse.pojo",
        "com.nse.service"})
@EnableMongoRepositories(basePackages = "com.nse.repository")

public class StockStrategyApplication {

    public static void main(String... args) {
        SpringApplication.run(StockStrategyApplication.class, args);
    }
}
