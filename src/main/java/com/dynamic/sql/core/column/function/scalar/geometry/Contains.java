package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;
import com.dynamic.sql.model.Point;
import com.dynamic.sql.utils.SqlUtils;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;

public class Contains extends ColumnFunctionDecorator implements ScalarFunction {

    private Point point;

    public <T, F> Contains(FieldFn<T, F> fn, Point point) {
        super(fn);
        this.point = point;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            String name = SqlUtils.extractQualifiedAlias(delegateFunction.getOriginColumnFn(), aliasTableMap, dataSourceName);
            registerValueWithKey(parameterBinder, delegateFunction.getOriginColumnFn(), value);
            return "ST_Contains(" + name + ", ST_PointFromText('" + point.toPointString() + "'," + point.getSrid() + "));";
        }
        throw FunctionException.unsupportedFunctionException("ST_Contains", sqlDialect);
    }
}