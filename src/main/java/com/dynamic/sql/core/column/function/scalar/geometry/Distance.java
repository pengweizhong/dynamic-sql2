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
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;
import com.dynamic.sql.model.Point;

public class Distance extends ColumnFunctionDecorator implements ScalarFunction {
    public Distance(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    private Point thisPoint;
    private Point otherPoint;

    public <T, F> Distance(FieldFn<T, F> fn, Point thisPoint) {
        super(fn);
        this.thisPoint = thisPoint;
    }

    public Distance(Point thisPoint, Point otherPoint) {
        this.thisPoint = thisPoint;
        this.otherPoint = otherPoint;
    }


    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            if (otherPoint == null) {
                return "ST_Distance(" + delegateFunction.getFunctionToString(sqlDialect, version)
                        + ",  ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + "))".concat(appendArithmeticSql(sqlDialect, version));
            }
            return "ST_Distance(ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + ")"
                    + ",  ST_GeomFromText('" + otherPoint.toPointString() + "', " + otherPoint.getSrid() + "))".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("ST_Distance", sqlDialect);
    }

}
