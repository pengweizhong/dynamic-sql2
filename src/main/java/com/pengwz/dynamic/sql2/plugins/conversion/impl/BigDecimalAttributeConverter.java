package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverterModel;

import java.math.BigDecimal;

public class BigDecimalAttributeConverter extends AttributeConverterModel<BigDecimal> {

    @Override
    public BigDecimal convertToEntityAttribute(Object dbData) {
        if (dbData instanceof String) {
            return new BigDecimal(dbData.toString());
        }
        if (dbData instanceof Number) {
            Number number = (Number) dbData;
            return BigDecimal.valueOf(number.doubleValue());
        }
        throw new IllegalArgumentException("Cannot convert value `" + dbData + "` to type BigDecimal");
    }
}