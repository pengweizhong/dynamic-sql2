package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

import java.io.Serializable;

public abstract class ColumnFunctionDecorator extends AbstractColumFunction
        implements Serializable {

    protected AbstractColumFunction delegateFunction;
    //count 1
    protected Integer value;
    protected ParameterBinder parameterBinder = new ParameterBinder();

    public ColumnFunctionDecorator() {
        delegateFunction = new AnonymousFunction();
    }

    public ColumnFunctionDecorator(AbstractColumFunction delegateFunction) {
        this.delegateFunction = delegateFunction;
    }

    public <T, F> ColumnFunctionDecorator(FieldFn<T, F> fn) {
        this.delegateFunction = new Column(null, fn);
    }

    public <T, F> ColumnFunctionDecorator(String tableAlias, FieldFn<T, F> fn) {
        this.delegateFunction = new Column(tableAlias, fn);
    }

    public ColumnFunctionDecorator(String tableAlias, String columnName) {
        this.delegateFunction = new Column(tableAlias, columnName);
    }

    public ColumnFunctionDecorator(int value) {
        this.value = value;
    }

    //窗口函数
//    public ColumnFunctionDecorator(WindowsFunction windowsFunction, Over over) {
//        this.delegateFunction = windowsFunction;
//    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return delegateFunction.getOriginColumnFn();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return parameterBinder.addParameterBinder(delegateFunction.getParameterBinder());
    }

    @Override
    public String getTableAlias() {
        return delegateFunction.getTableAlias();
    }

}
