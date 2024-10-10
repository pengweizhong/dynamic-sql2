package com.pengwz.dynamic.sql2.plugins.conversion;

public abstract class DefaultAttributeConverter<X, Y> implements AttributeConverter<X, Y> {
    // 用于将实体属性转换为数据库列的值
    @Override
    public Y convertToDatabaseColumn(X attribute) {
        if (attribute == null) {
            return null;
        }
        return doConvertToDatabaseColumn(attribute);
    }

    // 用于将数据库列的值转换为实体属性
    @Override
    public X convertToEntityAttribute(Y dbData) {
        if (dbData == null) {
            return null;
        }
        return doConvertToEntityAttribute(dbData);
    }

    // 抽象方法供子类实现具体的转换逻辑
    protected abstract Y doConvertToDatabaseColumn(X attribute);

    protected abstract X doConvertToEntityAttribute(Y dbData);
}
