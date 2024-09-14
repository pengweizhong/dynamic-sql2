package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.IColumnArithmetic;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.windows.IWindowsFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.io.Serializable;
import java.util.function.Consumer;

public abstract class ColumnFunctionDecorator
        implements IColumFunction, IColumnArithmetic, Serializable {
    protected IColumFunction delegateFunction;
    //count 1
    protected int value;

    public ColumnFunctionDecorator(IColumFunction delegateFunction) {
        this.delegateFunction = delegateFunction;
    }

    public <T, F> ColumnFunctionDecorator(Fn<T, F> fn) {
        this.delegateFunction = new Column(fn);
    }

    public ColumnFunctionDecorator(int value) {
        this.value = value;
    }

    //窗口函数
    public ColumnFunctionDecorator(IWindowsFunction windowsFunction, Over over) {
        this.delegateFunction = windowsFunction;
    }

    @Override
    public String getFunctionToString() {
        return delegateFunction.getFunctionToString();
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