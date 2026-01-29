/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows;

import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

public class Rank extends ColumnFunctionDecorator implements WindowsFunction {

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() ==  SqlDialect.ORACLE) {
            return "RANK(" + delegateFunction.render(context) + ")";
        }
        if (context.getSqlDialect() ==  SqlDialect.MYSQL) {
            return "rank(" + delegateFunction.render(context) + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("rank", context.getSqlDialect());

    }
}
