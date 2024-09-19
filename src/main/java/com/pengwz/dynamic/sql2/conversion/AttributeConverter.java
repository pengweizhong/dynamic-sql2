package com.pengwz.dynamic.sql2.conversion;

/**
 * 用于在实体属性和数据库列值之间进行转换的接口。
 *
 * @param <X> 实体属性的类型
 * @param <Y> 数据库列的类型
 */
public interface AttributeConverter<X, Y> {

    /**
     * 将实体属性值转换为数据库列的值。
     *
     * @param attribute 实体属性值
     * @return 转换后的数据库列值
     */
    Y convertToDatabaseColumn(X attribute);

    /**
     * 将数据库列的值转换为实体属性值。
     *
     * @param dbData 数据库列值
     * @return 转换后的实体属性值
     */
    X convertToEntityAttribute(Y dbData);
}
