package com.pengwz.dynamic.sql2.asserts;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

public class FunctionAssert {

    private FunctionAssert() {
    }

    /**
     * 抛出函数无法在当前数据库版本支持的异常。
     *
     * @param functionName 函数名称
     * @param majorVersion 主版本号
     * @param minorVersion 次版本号
     * @param patchVersion 补丁号
     * @throws UnsupportedOperationException 函数不受支持
     */
    public static void throwNotSupportedFunctionException(String functionName, Version version) {
        throw new UnsupportedOperationException(
                String.format("Function %s is not supported in version %d.%d.%d", functionName,
                        version.getMajorVersion(), version.getMinorVersion(), version.getPatchVersion())
        );
    }

    public static void throwNotSupportedSqlDialectException(String functionName, SqlDialect sqlDialect) {
        throw new UnsupportedOperationException(
                String.format("%s dialect does not support %s function", sqlDialect, functionName)
        );
    }

}
