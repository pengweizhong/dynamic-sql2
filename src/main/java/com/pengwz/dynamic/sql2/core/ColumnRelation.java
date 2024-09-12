package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;
import com.pengwz.dynamic.sql2.core.dml.select.QueryFieldExtractor;
import com.pengwz.dynamic.sql2.core.dml.select.TableRelation;

public class ColumnRelation extends QueryFieldExtractor {
    private IColumFunction iColumFunction;


    public ColumnRelation(IColumFunction iColumFunction) {
        this.iColumFunction = iColumFunction;
    }

    public <T, F> ColumnRelation(Fn<T, F> fn) {
        queryFields.add(new Column(fn));
    }


    @Override
    public <T, F> ColumnRelation column(Fn<T, F> fn) {
        return this.column(fn, null);
    }

    @Override
    public <T, F> ColumnRelation column(Fn<T, F> fn, String alias) {
        queryFields.add(new Column(fn));
        return this;
    }

    @Override
    public ColumnRelation column(IColumFunction iColumFunction) {
        return this.column(iColumFunction, null);
    }

    @Override
    public ColumnRelation column(IColumFunction iColumFunction, String alias) {
        System.out.println("测试函数结果 --> " + iColumFunction.getFunctionToString());
        queryFields.add(iColumFunction);
        return this;
    }

    @Override
    public <T> TableRelation<T> from(Class<T> tableClass) {
        TableRelation tableRelation;
        tableRelation = new TableRelation<>(tableClass);
        return tableRelation;
    }

}
