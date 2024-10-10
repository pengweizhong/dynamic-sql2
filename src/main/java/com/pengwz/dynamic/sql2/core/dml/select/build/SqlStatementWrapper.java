package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public class SqlStatementWrapper {
    private StringBuilder rawSql;
    private ParameterBinder parameterBinder;

    public SqlStatementWrapper(StringBuilder rawSql, ParameterBinder parameterBinder) {
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
