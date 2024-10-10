package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;

public class EnumAttributeConverter<E extends Enum<E>> extends DefaultAttributeConverter<Enum<E>, String> {
    private final Class<E> enumClass;

    // 构造函数接收枚举类的类型
    public EnumAttributeConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    protected String doConvertToDatabaseColumn(Enum<E> attribute) {
        return attribute.name();
    }

    @Override
    protected Enum<E> doConvertToEntityAttribute(String dbData) {
        //默认按照名字匹配
        String enumName = String.valueOf(dbData);
        return Enum.valueOf(enumClass, enumName);
    }

}
