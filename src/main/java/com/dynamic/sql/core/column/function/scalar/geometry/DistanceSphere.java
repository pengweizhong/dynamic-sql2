package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;
import com.dynamic.sql.model.Point;

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
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            if (otherPoint == null) {
                return "ST_Distance_Sphere(" + delegateFunction.getFunctionToString(sqlDialect, version)
                        + ",  ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + "))";
            }
            return "ST_Distance_Sphere(ST_GeomFromText('" + thisPoint.toPointString() + "', " + thisPoint.getSrid() + ")"
                    + ",  ST_GeomFromText('" + otherPoint.toPointString() + "', " + otherPoint.getSrid() + "))";
        }
        throw FunctionException.unsupportedFunctionException("ST_Distance_Sphere", sqlDialect);
    }

}
