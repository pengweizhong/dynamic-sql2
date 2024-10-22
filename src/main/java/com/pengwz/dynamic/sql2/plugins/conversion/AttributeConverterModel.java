package com.pengwz.dynamic.sql2.plugins.conversion;

public abstract class AttributeConverterModel<X> implements AttributeConverter<X, Object> {

    @Override
    public final Object convertToDatabaseColumn(X attribute) {
        throw new UnsupportedOperationException();
    }
}
