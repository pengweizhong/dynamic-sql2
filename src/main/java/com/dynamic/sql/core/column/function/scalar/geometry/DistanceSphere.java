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
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.model.Point;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

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
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            if (otherPoint == null) {
                return "ST_Distance_Sphere(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap)
                        + ",  ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + "))".concat(appendArithmeticSql(sqlDialect, version));
            }
            return "ST_Distance_Sphere(ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + ")"
                    + ",  ST_GeomFromText('" + otherPoint.toPointString() + "', " + otherPoint.getSrid() + "))".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw ExceptionUtils.unsupportedFunctionException("ST_Distance_Sphere", sqlDialect);
    }

}
