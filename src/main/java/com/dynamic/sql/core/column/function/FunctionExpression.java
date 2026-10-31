package com.dynamic.sql.core.column.function;

import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

/** 常规 MySQL 函数公共表达式：名称由子类固定，业务值统一绑定。 */
public abstract class FunctionExpression extends ColumnFunctionDecorator implements ScalarFunction {
    private final String name;
    protected final ExpressionArguments arguments=new ExpressionArguments();
    protected FunctionExpression(String name,Object... values) {
        this.name=name;
        //首个操作数为列表达式时继承其来源，保持既有实体属性别名映射。
        if(values.length>0 && values[0] instanceof AbstractColumFunction)
            delegateFunction=(AbstractColumFunction)values[0];
        for(Object value:values) arguments.add(value);
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException(name,context.getSqlDialect());
        StringBuilder sql=new StringBuilder(name).append("(");
        for(int i=0;i<arguments.size();i++) {
            if(i>0) sql.append(", ");
            sql.append(arguments.render(i,context));
        }
        return sql.append(")").append(appendArithmeticSql(context)).toString();
    }
    @Override public ParameterBinder parameterBinder() {
        return arguments.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
