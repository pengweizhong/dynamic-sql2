package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.ColumnArithmetic;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

import java.io.Serializable;
import java.util.function.Consumer;

public abstract class ColumnFunctionDecorator
        implements ColumFunction, ColumnArithmetic, Serializable {

    protected ColumFunction delegateFunction;
    //count 1
    protected int value;

    protected ParameterBinder parameterBinder = new ParameterBinder();

    public ColumnFunctionDecorator(ColumFunction delegateFunction) {
        this.delegateFunction = delegateFunction;
    }

    public <T, F> ColumnFunctionDecorator(Fn<T, F> fn) {
        this.delegateFunction = new Column(fn);
    }

    public ColumnFunctionDecorator(int value) {
        this.value = value;
    }

    //窗口函数
    public ColumnFunctionDecorator(WindowsFunction windowsFunction, Over over) {
        this.delegateFunction = windowsFunction;
    }

    @Override
    public Fn<?, ?> getoriginColumnFn() {
        return delegateFunction.getoriginColumnFn();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    @Override
    public <T, F> ColumnFunctionDecorator add(Fn<T, F> column) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator add(Number value) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator add(Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> ColumnFunctionDecorator subtract(Fn<T, F> column) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator subtract(Number value) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator subtract(Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> ColumnFunctionDecorator multiply(Fn<T, F> column) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator multiply(Number value) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator multiply(Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> ColumnFunctionDecorator divide(Fn<T, F> column) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator divide(Number value) {
        return null;
    }

    @Override
    public ColumnFunctionDecorator divide(Consumer<NestedSelect> nestedSelect) {
        return null;
    }

}
