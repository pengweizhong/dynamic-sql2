package com.pengwz.dynamic.sql2.conversion;

import java.util.HashMap;
import java.util.Map;

public abstract class EnumConverter<E extends Enum<E>, V> implements AttributeConverter<E, V> {

    private final Map<V, E> toEnum = new HashMap<>();
    private final Map<E, V> toDbValue = new HashMap<>();

    // 初始化映射
    public EnumConverter(Map<V, E> mapping) {
        mapping.forEach((key, value) -> {
            toEnum.put(key, value);
            toDbValue.put(value, key);
        });
    }

    // 将数据库中的值转换为枚举
    @Override
    public E convertToEntityAttribute(V dbData) {
        return toEnum.getOrDefault(dbData, null);
    }

    // 将枚举转换为数据库值
    @Override
    public V convertToDatabaseColumn(E attribute) {
        return toDbValue.getOrDefault(attribute, null);
    }
}