package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractColumnReference {
    protected List<IColumFunction> queryFields = new ArrayList<>();

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn);

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn, String alias);

    public abstract AbstractColumnReference column(IColumFunction iColumFunction);

    public abstract AbstractColumnReference column(IColumFunction iColumFunction, String alias);

    public abstract <T> TableRelation<T> from(Class<T> tableClass);

}
