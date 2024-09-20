package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.column.Column;

public interface ColumFunction extends Column {

    String getMySqlFunction();

    default String getOracleFunction() {
        throw new UnsupportedOperationException("ORACLE 先不做");
    }

    @Override
    default String getColumnName() {
//        return getFunctionToString();
        throw new UnsupportedOperationException();
    }

}
