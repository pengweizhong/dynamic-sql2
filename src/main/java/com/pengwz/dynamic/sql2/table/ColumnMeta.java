package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;

import java.lang.reflect.Field;

public class ColumnMeta {
    //数据库字段名
    private String columnName;
    //是否为主键
    private boolean isPrimary;
    //主键生成策略
    private GeneratedStrategy generatedStrategy;
    //实体类字段
    private Field field;

    private Class<? extends AttributeConverter> converter;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public GeneratedStrategy getGeneratedStrategy() {
        return generatedStrategy;
    }

    public void setGeneratedStrategy(GeneratedStrategy generatedStrategy) {
        this.generatedStrategy = generatedStrategy;
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
