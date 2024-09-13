package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.TableRelation;

import java.util.function.Consumer;

public class ColumnReference extends AbstractColumnReference {
    private IColumFunction iColumFunction;

    public ColumnReference() {
    }

    public ColumnReference(IColumFunction iColumFunction) {
        this.iColumFunction = iColumFunction;
    }

    public <T, F> ColumnReference(Fn<T, F> fn) {
        queryFields.add(new Column(fn));
    }


    @Override
    public AbstractColumnReference one() {
        return this;
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn) {
        return this.column(fn, null);
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn, String alias) {
        queryFields.add(new Column(fn));
        return this;
    }

    @Override
    public ColumnReference column(IColumFunction iColumFunction) {
        return this.column(iColumFunction, null);
    }

    @Override
    public ColumnReference column(IColumFunction iColumFunction, String alias) {
        System.out.println("测试函数结果 --> " + iColumFunction.getFunctionToString());
        queryFields.add(iColumFunction);
        return this;
    }

    @Override
    public AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String alias) {
        return this;
    }

    @Override
    public <T> TableRelation<T> from(Class<T> tableClass) {
        TableRelation tableRelation;
        tableRelation = new TableRelation<>(tableClass);
        return tableRelation;
    }

}
