/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows.aggregate;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.ExpressionArguments;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;


public class Count extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {
    private ExpressionArguments distinctArguments;
    /** MySQL 多列 COUNT 仅提供 DISTINCT 形式，NULL 行由数据库排除。 */
    public static Count distinct(Object first,Object... others) {
        Count result=new Count(1);
        result.distinctArguments=new ExpressionArguments();result.distinctArguments.add(first);
        for(Object other:others) result.distinctArguments.add(other);
        return result;
    }
    @Override public ParameterBinder parameterBinder() {
        return distinctArguments==null ? super.parameterBinder()
                : distinctArguments.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }


    public Count(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Count(FieldFn<T, F> fn) {
        super(fn);
    }

    public Count(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    public <T, F> Count(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Count(int value) {
        super(value);
    }

    @Override
    public String render(RenderContext context) {
        if(distinctArguments!=null) {
            if(context.getSqlDialect()!=SqlDialect.MYSQL)
                throw ExceptionUtils.unsupportedFunctionException("COUNT DISTINCT",context.getSqlDialect());
            StringBuilder sql=new StringBuilder("COUNT(DISTINCT ");
            for(int i=0;i<distinctArguments.size();i++) {
                if(i>0) sql.append(", ");sql.append(distinctArguments.render(i,context));
            }
            return sql.append(")").append(appendArithmeticSql(context)).toString();
        }
        if (context.getSqlDialect() ==  SqlDialect.ORACLE) {
            if (value != null) {
                return "COUNT(" + value + ")".concat(appendArithmeticSql(context));
            }
            return "COUNT(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        if (context.getSqlDialect() ==  SqlDialect.MYSQL) {
            if (value != null) {
                return "count(" + value + ")".concat(appendArithmeticSql(context));
            }
            return "count(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        throw ExceptionUtils.unsupportedFunctionException("count", context.getSqlDialect());
    }
}
