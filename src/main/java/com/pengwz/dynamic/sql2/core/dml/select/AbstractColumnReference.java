package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;

import java.util.function.Consumer;

/**
 * 该抽象类提供了 SQL 查询列的引用及其相关操作的基础实现。
 * 包括选择特定列、应用函数、使用别名等。
 */
public abstract class AbstractColumnReference {
    protected final SelectSpecification selectSpecification;

    protected AbstractColumnReference(SelectSpecification selectSpecification) {
        this.selectSpecification = selectSpecification;
    }

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn);

    public abstract <T, F> AbstractColumnReference column(String tableAlias, Fn<T, F> fn);

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn, String alias);

    public abstract <T, F> AbstractColumnReference column(String tableAlias, Fn<T, F> fn, String alias);

    public abstract AbstractColumnReference column(ColumFunction iColumFunction);

    public abstract AbstractColumnReference column(ColumFunction iColumFunction, String alias);

    public abstract AbstractColumnReference column(WindowsFunction windowsFunction, Over over, String alias);

    public abstract AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String alias);

    public abstract AbstractColumnReference allColumn();

    public abstract AbstractColumnReference allColumn(Class<?> tableClass);

    public abstract <T> TableRelation<T> from(Class<T> tableClass);

    public abstract <T> TableRelation<T> from(Class<T> tableClass, String alias);

    public abstract TableRelation<?> from(CteTable cteTable);

}
