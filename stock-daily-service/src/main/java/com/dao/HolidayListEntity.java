package com.dao;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("holiday_list")
public class HolidayListEntity {

    private Date holiday_date;

    private String holiday;

    public Date getHoliday_date() {
        return holiday_date;
    }

    public void setHoliday_date(Date holiday_date) {
        this.holiday_date = holiday_date;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }
}
