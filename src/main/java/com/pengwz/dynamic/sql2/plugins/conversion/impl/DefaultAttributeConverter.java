package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;

public class DefaultAttributeConverter<X, Y> implements AttributeConverter<X, Y> {
    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return null;
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return null;
    }
}
