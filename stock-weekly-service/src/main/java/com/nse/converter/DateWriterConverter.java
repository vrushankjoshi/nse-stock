package com.nse.converter;


import org.springframework.core.convert.converter.Converter;

public class DateWriterConverter implements Converter<java.util.Date, java.sql.Date> {

    public java.sql.Date convert(java.util.Date utilDate) {
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        return sqlDate;
    }
}
