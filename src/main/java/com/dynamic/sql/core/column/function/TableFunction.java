package com.dynamic.sql.core.column.function;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

/**
 * 类似查询表级函数，可以在from表时使用
 */
public interface TableFunction {
    String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException;

    Fn<?, ?> getOriginColumnFn();

    ParameterBinder getParameterBinder();
}
