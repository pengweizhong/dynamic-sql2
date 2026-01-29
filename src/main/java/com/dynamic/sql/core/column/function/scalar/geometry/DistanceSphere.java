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
import com.dynamic.sql.model.Point;
import com.dynamic.sql.utils.ExceptionUtils;

public class DistanceSphere extends ColumnFunctionDecorator implements ScalarFunction {
    public DistanceSphere(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    private Point thisPoint;
    private Point otherPoint;

    public <T, F> DistanceSphere(FieldFn<T, F> fn, Point thisPoint) {
        super(fn);
        this.thisPoint = thisPoint;
    }

    public DistanceSphere(Point thisPoint, Point otherPoint) {
        this.thisPoint = thisPoint;
        this.otherPoint = otherPoint;
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            if (otherPoint == null) {
                return "ST_Distance_Sphere(" + delegateFunction.render(context)
                        + ",  ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + "))".concat(appendArithmeticSql(context));
            }
            return "ST_Distance_Sphere(ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + ")"
                    + ",  ST_GeomFromText('" + otherPoint.toPointString() + "', " + otherPoint.getSrid() + "))".concat(appendArithmeticSql(context));
        }
        throw ExceptionUtils.unsupportedFunctionException("ST_Distance_Sphere", context.getSqlDialect());
    }
}
