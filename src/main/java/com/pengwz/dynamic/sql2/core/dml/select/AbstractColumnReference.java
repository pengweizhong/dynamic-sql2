package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractColumnReference {
    protected List<IColumFunction> queryFields = new ArrayList<>();

    //简简单单的  select 1 from ...
    public abstract AbstractColumnReference one();

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn);

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn, String alias);

    public abstract AbstractColumnReference column(IColumFunction iColumFunction);

    public abstract AbstractColumnReference column(IColumFunction iColumFunction, String alias);

    public abstract AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String alias);

    public AbstractColumnReference allColumn() {
        return new AllColumnReference();
    }

    public abstract <T> TableRelation<T> from(Class<T> tableClass);

    public static class AllColumnReference extends ColumnReference {
        public AllColumnReference() {
            queryFields.add(new Column("*"));
        }
    }
}
