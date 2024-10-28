package com.pengwz.dynamic.sql2.plugins.conversion.attribute;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class LocalDateAttributeConverter extends AttributeConverterModel<LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateAttributeConverter.class);

    @Override
    public Object convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null) {
            return null;
        }
        return java.sql.Date.valueOf(attribute);
    }

    @Override
    public LocalDate convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof java.sql.Date) {
            java.sql.Date sqlDate = (java.sql.Date) dbData;
            return sqlDate.toLocalDate();
        }
        if (dbData instanceof java.sql.Timestamp) {
            java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) dbData;
            return sqlTimestamp.toLocalDateTime().toLocalDate();
        }
        throw new IllegalArgumentException("Cannot convert value `" + dbData + "` to type LocalDate");
    }
}