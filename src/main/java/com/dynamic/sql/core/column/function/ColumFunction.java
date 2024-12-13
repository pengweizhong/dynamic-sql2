package com.dynamic.sql.core.column.function;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

/**
 * 一般情况下实现者不能直接实现该类，需要先继承AbstractColumFunction，因为AbstractColumFunction附带列运算
 */
public interface ColumFunction {

    String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException;

    Fn<?, ?> getOriginColumnFn();

    ParameterBinder getParameterBinder();

    String getTableAlias();

    default void setTableAlias(String tableAlias) {
    }
}
