package com.pengwz.dynamic.sql2.core.dml;

public abstract class AbstractSqlWrapper {
    private final String dataSourceName;

    protected AbstractSqlWrapper(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}
