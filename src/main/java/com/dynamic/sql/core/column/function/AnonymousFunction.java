package com.dynamic.sql.core.column.function;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

/**
 * 匿名函数，当明确不需要函数调用时使用，此类存在的目的是为了统一函数调用行为
 */
public class AnonymousFunction extends AbstractColumFunction {

    public AnonymousFunction() {
    }

    /**
     * 仅仅记录函数，不做任何特殊处理
     */
    public AnonymousFunction(String functionToString, ParameterBinder parameterBinder) {
//        arithmeticSql.append(functionToString);
//        arithmeticParameterBinder.addParameterBinder(parameterBinder);
    }

    public String getFunctionToString() {
//        return arithmeticSql.toString();
        return "ANONYMOUS";
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        return "";
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return null;
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return "";
    }
}
