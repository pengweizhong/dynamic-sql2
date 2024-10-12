package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;

public class StringAttributeConverter extends DefaultAttributeConverter<String, String> {


    @Override
    protected String doConvertToDatabaseColumn(String attribute) {
        return attribute;
    }

    @Override
    protected String doConvertToEntityAttribute(String dbData) {
        return dbData;
    }
}