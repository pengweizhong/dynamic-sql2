package com.dynamic.sql.plugins.conversion;


public class DefaultAttributeConverter implements AttributeConverter {
    @Override
    public Object convertToDatabaseColumn(Object attribute) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertToEntityAttribute(Object dbData) {
        throw new UnsupportedOperationException();
    }
}
