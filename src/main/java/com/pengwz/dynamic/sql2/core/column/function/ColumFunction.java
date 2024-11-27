package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

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
