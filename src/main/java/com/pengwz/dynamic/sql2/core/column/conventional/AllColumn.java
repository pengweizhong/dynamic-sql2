package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

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
