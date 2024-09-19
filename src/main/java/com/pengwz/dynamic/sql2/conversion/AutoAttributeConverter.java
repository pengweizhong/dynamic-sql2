package com.pengwz.dynamic.sql2.conversion;

public class AutoAttributeConverter<X,Y> implements AttributeConverter<X,Y>{
    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return null;
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return null;
    }
}
