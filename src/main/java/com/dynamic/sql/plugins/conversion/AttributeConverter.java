package com.dynamic.sql.plugins.conversion;

/**
 * 用于在实体属性和数据库列值之间进行转换的接口。
 *
 * @param <X> 实体属性的类型
 * @param <Y> 数据库列的类型
 */
public interface AttributeConverter<X, Y> {
    /**
     * 判断是否跳过将实体属性值转换为数据库列的值的操作。<br>
     * 默认情况下，返回 false 表示不跳过转换。<br>
     * 如果需要根据某些条件决定是否跳过该转换，子类可以重写该方法。<br>
     * 若跳过，则会使用原生值写入到数据库
     *
     * @param attribute 实体属性值
     * @return 如果跳过转换，返回 true；否则返回 false。
     */
    default boolean isSkipConvertToDatabaseColumn(Object attribute) {
        return false;
    }

    /**
     * 将实体属性值转换为数据库列的值。
     *
     * @param attribute 实体属性值
     * @return 转换后的数据库列值
     */
    Y convertToDatabaseColumn(X attribute);

    /**
     * 判断是否跳过将数据库列的值转换为实体属性值的操作。<br>
     * 默认情况下，返回 false 表示不跳过转换。<br>
     * 如果需要根据某些条件决定是否跳过该转换，子类可以重写该方法。<br>
     * 若跳过，则会使用原生值写入到实体类
     *
     * @param dbData 数据库列值
     * @return 如果跳过转换，返回 true；否则返回 false。
     */
    default boolean isSkipConvertToEntityAttribute(Object dbData) {
        return false;
    }

    /**
     * 将数据库列的值转换为实体属性值。
     *
     * @param dbData 数据库列值
     * @return 转换后的实体属性值
     */
    X convertToEntityAttribute(Y dbData);
}
