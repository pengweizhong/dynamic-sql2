package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

/**
 * 匿名函数，当明确不需要函数调用时使用，此类存在的目的是为了统一函数调用行为
 */
public class AnonymousFunction implements ColumFunction{
    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        return "";
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return null;
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return "";
    }
}
