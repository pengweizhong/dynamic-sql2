package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;
import com.pengwz.dynamic.sql2.core.crud.select.QueryFieldExtractor;

public class ColumnRelation extends QueryFieldExtractor {
    private IColumFunction iColumFunction;
    private TableRelation tableRelation;

    public ColumnRelation(IColumFunction iColumFunction) {
        this.iColumFunction = iColumFunction;
    }

    public <T, F> ColumnRelation(Fn<T, F> fn) {
        queryFields.add(new Column(fn));
    }


    @Override
    public <T, F> ColumnRelation column(Fn<T, F> fn) {
        queryFields.add(new Column(fn));
        return this;
    }

    @Override
    public ColumnRelation column(IColumFunction iColumFunction) {
        System.out.println("测试函数结果 --> " + iColumFunction.getFunctionToString());
        queryFields.add(iColumFunction);
        return this;
    }

    @Override
    public <T> TableRelation from(Class<T> tableClass) {
        tableRelation = new TableRelation(tableClass);
        return tableRelation;
    }

    public static class AlisaRelation {


    }
}
