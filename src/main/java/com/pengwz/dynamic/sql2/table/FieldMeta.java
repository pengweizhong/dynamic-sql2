package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;

import java.lang.reflect.Field;

public class FieldMeta {
    //数据库字段名
    private String columnName;
    //实体类字段
    private Field field;
    //转换策略
    private Class<? extends AttributeConverter> converter;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class<? extends AttributeConverter> getConverter() {
        return converter;
    }

    public void setConverter(Class<? extends AttributeConverter> converter) {
        this.converter = converter;
    }
}