package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Select extends AbstractColumnReference {

    public ColumnReference allColumn() {
        queryFields.add(new Column("*"));
        return new ColumnReference(new Column("*"));
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn) {
        return column(fn, null);
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn, String alias) {
        ColumnReference ColumnReference = new ColumnReference(fn);
        return ColumnReference;
    }

    @Override
    public ColumnReference column(IColumFunction iColumFunction) {
        return this.column(iColumFunction, null);
    }


    @Override
    public ColumnReference column(IColumFunction iColumFunction, String alias) {
        System.out.println("测试函数结果 --> " + iColumFunction.getFunctionToString());
        ColumnReference ColumnReference = new ColumnReference(iColumFunction);
        queryFields.add(iColumFunction);
        return ColumnReference;
    }

    @Override
    public <T> TableRelation<T> from(Class<T> tableClass) {
        TableRelation tableRelation = new TableRelation(tableClass);
        return tableRelation;
    }

}
