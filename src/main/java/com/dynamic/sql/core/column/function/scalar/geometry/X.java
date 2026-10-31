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
import com.dynamic.sql.utils.SqlUtils;


/** 读取点的第一坐标分量，不解释为经度；当前仅支持 MySQL，其他方言明确拒绝。 */
public class X extends ColumnFunctionDecorator implements ScalarFunction {
    private String coordinate;
    /** 设置坐标分量，地理范围及轴序由数据库检查。 */
    public X(AbstractColumFunction geometry, double coordinate) { super(geometry); this.coordinate = SqlUtils.registerValueWithKey(parameterBinder, coordinate); }
    public X(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> X(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> X(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            return "ST_X(" + delegateFunction.render(context) + (coordinate == null ? "" : ", " + coordinate) + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("ST_X", context.getSqlDialect());
    }
}
