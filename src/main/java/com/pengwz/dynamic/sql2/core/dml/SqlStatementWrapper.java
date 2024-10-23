package com.pengwz.dynamic.sql2.core.dml;

import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public class SqlStatementWrapper {
    private String dataSourceName;
    private StringBuilder rawSql;
    private ParameterBinder parameterBinder;

    public SqlStatementWrapper(String dataSourceName, StringBuilder rawSql, ParameterBinder parameterBinder) {
        this.dataSourceName = dataSourceName;
        this.rawSql = rawSql;
        this.parameterBinder = parameterBinder;
    }

    public StringBuilder getRawSql() {
        return rawSql;
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}
