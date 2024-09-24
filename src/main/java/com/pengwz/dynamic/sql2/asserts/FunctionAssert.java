package com.pengwz.dynamic.sql2.asserts;

import com.pengwz.dynamic.sql2.core.Version;

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
    public static void throwNotSupportedException(String functionName) {
        throw new UnsupportedOperationException(
                String.format("Function %s is not supported in version %d.%d.%d", functionName,
                        Version.getMajorVersion(), Version.getMinorVersion(), Version.getPatchVersion())
        );
    }


}
