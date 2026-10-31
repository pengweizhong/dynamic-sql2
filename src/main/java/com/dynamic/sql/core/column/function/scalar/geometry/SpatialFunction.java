package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.utils.SqlUtils;

/** 空间函数的有序参数模型：表达式递归渲染，业务常量始终绑定，函数名仅来自内部实现。 */
abstract class SpatialFunction extends ColumnFunctionDecorator implements ScalarFunction {
    private final String name;
    private final Object[] arguments;
    private final String[] placeholders;

    /** 拷贝调用参数，避免外部数组修改改变已构造查询。 */
    protected SpatialFunction(String name, Object... arguments) {
        this.name = name;
        this.arguments = arguments.clone();
        this.placeholders = new String[arguments.length];
        for (int i=0; i<arguments.length; i++) {
            if (!(arguments[i] instanceof AbstractColumFunction))
                placeholders[i] = SqlUtils.registerValueWithKey(parameterBinder, arguments[i]);
        }
    }

    @Override public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException(name, context.getSqlDialect());
        StringBuilder sql = new StringBuilder(name).append("(");
        for (int i=0; i<arguments.length; i++) {
            if (i>0) sql.append(", ");
            sql.append(arguments[i] instanceof AbstractColumFunction
                    ? ((AbstractColumFunction)arguments[i]).render(context) : placeholders[i]);
        }
        return sql.append(")").append(appendArithmeticSql(context)).toString();
    }

    /** 按 SQL 出现顺序收集嵌套和常量参数，重复执行不修改原容器。 */
    @Override public ParameterBinder parameterBinder() {
        ParameterBinder binder = new ParameterBinder();
        for (int i=0; i<arguments.length; i++) {
            if (arguments[i] instanceof AbstractColumFunction)
                binder.addParameterBinder(((AbstractColumFunction)arguments[i]).parameterBinder());
            else binder.add(placeholders[i], parameterBinder.getValue(placeholders[i]));
        }
        binder.addParameterBinder(getArithmetic().getArithmeticParameterBinder());
        return binder;
    }
}
