package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.enums.GenerationType;

import java.lang.reflect.Field;

class ColumnMeta {
    //数据库字段名
    private String columnName;
    //是否为主键
    private boolean isPrimary;
    //主键生成策略
    private GenerationType generationType;
    //实体类字段
    private Field field;

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

    public GenerationType getGenerationType() {
        return generationType;
    }

    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "ColumnMeta{" +
                "columnName='" + columnName + '\'' +
                ", isPrimary=" + isPrimary +
                ", generationType=" + generationType +
                ", field=" + field.getName() +
                '}';
    }
}
