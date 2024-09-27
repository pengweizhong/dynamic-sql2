package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

public interface ColumFunction {

    String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException;

}
