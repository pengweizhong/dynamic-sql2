/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.exception;


import com.dynamic.sql.core.Version;
import com.dynamic.sql.enums.SqlDialect;

public class FunctionException {

    private FunctionException() {
    }

    public static UnsupportedOperationException unsupportedFunctionException(String functionName, Version version, SqlDialect sqlDialect) {
        throw new UnsupportedOperationException(
                String.format("%s Function `%s` is not supported in version %d.%d.%d", sqlDialect, functionName,
                        version.getMajorVersion(), version.getMinorVersion(), version.getPatchVersion())
        );
    }

    public static UnsupportedOperationException unsupportedFunctionException(String functionName, SqlDialect sqlDialect) {
        throw new UnsupportedOperationException(
                String.format("%s dialect does not support `%s` function", sqlDialect, functionName)
        );
    }
}
