package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.ColumnRelation;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryFieldExtractor {
    protected List<IColumFunction> queryFields = new ArrayList<>();

    public abstract <T, F> ColumnRelation column(Fn<T, F> fn);

    public abstract ColumnRelation column(IColumFunction iColumFunction);

    public QueryFieldExtractor as(String aliasName) {
        return this;
    }

    public abstract <T> TableRelation from(Class<T> tableClass);

}
