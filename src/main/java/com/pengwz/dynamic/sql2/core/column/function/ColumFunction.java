package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.column.Column;

public interface ColumFunction extends Column {
    /**
     * 获取与 MySQL 数据库版本相关的函数。
     *
     * @param majorVersionNumber MySQL 数据库的主版本号，例如 8。
     * @param minorVersionNumber MySQL 数据库的次版本号，例如 0。
     * @param patchVersionNumber MySQL 数据库的补丁号，例如 25。
     * @return 返回与指定版本号相对应的 MySQL 函数名。
     * @throws UnsupportedOperationException 如果当前实现不支持获取 MySQL 函数。
     */
    String getMySqlFunction(int majorVersionNumber, int minorVersionNumber, int patchVersionNumber);

    default String getOracleFunction() {
        throw new UnsupportedOperationException("ORACLE 先不做");
    }

    @Override
    default String getColumnName() {
//        return getFunctionToString();
        throw new UnsupportedOperationException();
    }

}
