package com.pengwz.dynamic.sql2.plugins.conversion;

public interface EnumConverter<E extends Enum<E>, V> extends AttributeConverter<E, V> {

    /**
     * 将给定的枚举属性转换为相应的数据库值。
     *
     * @param attribute 要转换的枚举属性
     * @return 转换后的数据库值，用于写库操作
     */
    V toDatabaseValue(E attribute);

    /**
     * 将给定的数据库值转换为相应的枚举属性。
     *
     * @param dbData 要转换的数据库值
     * @return 转换后的枚举属性，用于读库操作
     */
    E fromDatabaseValue(V dbData);

    @Override
    default V convertToDatabaseColumn(E attribute) {
        return toDatabaseValue(attribute);
    }

    @Override
    default E convertToEntityAttribute(V dbData) {
        return fromDatabaseValue(dbData);
    }

}