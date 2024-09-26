package com.pengwz.dynamic.sql2.core.dml.select.build;

public class AllColumnInfo {
    private Class<?> tableClass;

    public AllColumnInfo(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

}
