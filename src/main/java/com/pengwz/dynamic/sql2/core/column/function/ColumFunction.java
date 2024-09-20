package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.column.Column;

public interface ColumFunction extends Column {
    /**
     * 获取与 MySQL 数据库版本相关的函数。
     *
     * @throws UnsupportedOperationException 如果当前实现不支持获取 MySQL 函数。
     */
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
