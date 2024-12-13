package com.dynamic.sql.core.column.conventional;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

public final class AllColumn implements ColumFunction {

    private final Class<?> tableClass;
    private String tableAlias;

    public AllColumn(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public AllColumn(String tableAlias, Class<?> tableClass) {
        this.tableClass = tableClass;
        this.tableAlias = tableAlias;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }
}
