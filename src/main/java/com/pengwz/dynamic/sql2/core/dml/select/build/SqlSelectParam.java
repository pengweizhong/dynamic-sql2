package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public class SqlSelectParam {
    private StringBuilder sql;
    private ParameterBinder parameterBinder;

    public SqlSelectParam(StringBuilder sql, ParameterBinder parameterBinder) {
        this.sql = sql;
        this.parameterBinder = parameterBinder;
    }

    public StringBuilder getSql() {
        return sql;
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }
}
