package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;

public class NumberAttributeConverter extends DefaultAttributeConverter<Number, Number> {


    @Override
    protected Number doConvertToDatabaseColumn(Number attribute) {
        return attribute;
    }

    @Override
    protected Number doConvertToEntityAttribute(Number dbData) {
        return dbData;
    }
}