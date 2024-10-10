package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAttributeConverter extends DefaultAttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    protected String doConvertToDatabaseColumn(LocalDate attribute) {
        return attribute.format(FORMATTER);
    }

    @Override
    protected LocalDate doConvertToEntityAttribute(String dbData) {
        return LocalDate.parse(dbData, FORMATTER);
    }
}