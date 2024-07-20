package com.nse.converter;


import org.springframework.core.convert.converter.Converter;

public class DateReadConverter implements Converter<java.sql.Date, java.util.Date> {

    @Override
    public java.util.Date convert(java.sql.Date sqlDate) {
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());

        return utilDate;
    }

}
