package com.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document("stock_processing_day")
public class ProcessingDay implements Serializable {

    @Id
    private String name;

    private Date processingDay;


    private Date weekProcessingDay;

    private Date weekLastCalcukationDay;

    private Date dailyLastCalcukationDay;

    private Date updatedDate;

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getProcessingDay() {
        return processingDay;
    }

    public void setProcessingDay(Date processingDay) {
        this.processingDay = processingDay;
    }

    public Date getWeekProcessingDay() {
        return weekProcessingDay;
    }

    public void setWeekProcessingDay(Date weekProcessingDay) {
        this.weekProcessingDay = weekProcessingDay;
    }

    public Date getWeekLastCalcukationDay() {
        return weekLastCalcukationDay;
    }

    public void setWeekLastCalcukationDay(Date weekLastCalcukationDay) {
        this.weekLastCalcukationDay = weekLastCalcukationDay;
    }

    public Date getDailyLastCalcukationDay() {
        return dailyLastCalcukationDay;
    }

    public void setDailyLastCalcukationDay(Date dailyLastCalcukationDay) {
        this.dailyLastCalcukationDay = dailyLastCalcukationDay;
    }
}
