package com.dynamic.sql.table.cte;

import java.lang.reflect.Field;

public class CTEColumnMeta {
    //数据库字段名
    private String columnName;
    //实体类字段
    private Field field;

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

}
