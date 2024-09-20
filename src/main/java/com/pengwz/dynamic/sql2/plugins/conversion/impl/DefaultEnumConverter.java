package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.EnumConverter;

public class DefaultEnumConverter<E extends Enum<E>> implements EnumConverter<E, Object> {
    @Override
    public Object toDatabaseValue(E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public E fromDatabaseValue(Object dbData) {
        throw new UnsupportedOperationException();
    }

    public E fromDatabaseValue(Class<E> enumClass, Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (enumClass == null) {
            throw new IllegalArgumentException("enumClass must not be null");
        }
        //默认按照名字匹配
        String enumName = String.valueOf(dbData);
        return Enum.valueOf(enumClass, enumName);
    }
}
