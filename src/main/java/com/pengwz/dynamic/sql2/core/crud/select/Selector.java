package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.ColumnRelation;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.TableRelation;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Selector extends QueryFieldExtractor {

    private Selector() {
    }

    public static Selector instance() {
        Selector selector = new Selector();
        return selector;
    }

    public ColumnRelation allColumn() {
        queryFields.add(new Column("*"));
        return new ColumnRelation(new Column("*"));
    }

    @Override
    public <T, F> ColumnRelation column(Fn<T, F> fn) {
        ColumnRelation columnRelation = new ColumnRelation(fn);
        queryFields.add(new Column(fn));
        return columnRelation;
    }

    @Override
    public ColumnRelation column(IColumFunction iColumFunction) {
        System.out.println("测试函数结果 --> " + iColumFunction.getFunctionToString());
        ColumnRelation columnRelation = new ColumnRelation(iColumFunction);
        queryFields.add(iColumFunction);
        return columnRelation;
    }

    @Override
    public <T> TableRelation from(Class<T> tableClass) {
        TableRelation tableRelation = new TableRelation(tableClass);
        return tableRelation;
    }

}
