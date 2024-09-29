package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public class SqlSelectParam {
    private StringBuilder rawSql;
    private ParameterBinder parameterBinder;

    public SqlSelectParam(StringBuilder rawSql, ParameterBinder parameterBinder) {
        this.rawSql = rawSql;
        this.parameterBinder = parameterBinder;
    }

    public StringBuilder getRawSql() {
        return rawSql;
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }
}
