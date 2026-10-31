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
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;


public class SRID extends ColumnFunctionDecorator implements ScalarFunction {
    //设置 SRID 只修改坐标系标识，不执行坐标转换。
    private Integer targetSrid;
    /** 字段设置 SRID 的便捷入口，保持与表达式入口相同语义。 */
    public <T,F> SRID(FieldFn<T,F> field, int srid) { this(new Column(field), srid); }


    /** 设置表达式的 SRID；坐标转换应使用独立的 Transform 函数。 */
    public SRID(AbstractColumFunction geometry, int srid) {
        super(geometry);
        this.targetSrid = srid;
    }


    public SRID(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> SRID(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> SRID(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            return "ST_SRID(" + delegateFunction.render(context) + (targetSrid == null ? "" : ", " + targetSrid) + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("SRID", context.getSqlDialect());
    }
}
