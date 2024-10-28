package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LocalDateAttributeConverter extends AttributeConverterModel<LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateAttributeConverter.class);

    @Override
    public LocalDate convertToEntityAttribute(Object dbData) {
        try {
//            if (dbData instanceof String) {
//                return new BigDecimal(dbData.toString());
//            }
//            if (dbData instanceof Number) {
//                Number number = (Number) dbData;
//                return BigDecimal.valueOf(number.doubleValue());
//            }
        } catch (Exception e) {
            log.error("", e);
        }
        throw new IllegalArgumentException("Cannot convert value `" + dbData + "` to type BigDecimal");
    }
}