/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

/** 判断几何对象是否为空；当前仅支持 MySQL，其他方言明确拒绝。 */
public class IsEmpty extends ColumnFunctionDecorator implements ScalarFunction {
    public IsEmpty(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> IsEmpty(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> IsEmpty(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            return "ST_IsEmpty(" + delegateFunction.render(context) + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("ST_IsEmpty", context.getSqlDialect());
    }
}
