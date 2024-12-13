package com.dynamic.sql.core.dml.select.build;


import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;

public class SqlStatementSelectWrapper extends SqlStatementWrapper {

    private Class<?> guessTheTargetClass;

    public SqlStatementSelectWrapper(String dataSourceName, StringBuilder rawSql,
                                     ParameterBinder parameterBinder, Class<?> guessTheTargetClass) {
        super(dataSourceName, rawSql, parameterBinder);
        this.guessTheTargetClass = guessTheTargetClass;
    }

    public Class<?> getGuessTheTargetClass() {
        return guessTheTargetClass;
    }
}
