package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.FieldFn;
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

    public abstract <T, F> AbstractColumnReference column(String tableAlias, FieldFn<T, F> fn);

    public abstract <T, F> AbstractColumnReference column(FieldFn<T, F> fn, String columnAlias);

    public abstract <T, F> AbstractColumnReference column(String tableAlias, FieldFn<T, F> fn, String columnAlias);

    public abstract AbstractColumnReference column(String tableAlias, String columnName);

    public abstract AbstractColumnReference column(String tableAlias, String columnName, String columnAlias);

    public abstract AbstractColumnReference column(ColumFunction iColumFunction);

    public abstract AbstractColumnReference column(ColumFunction iColumFunction, String columnAlias);

    public abstract AbstractColumnReference column(WindowsFunction windowsFunction, Over over, String columnAlias);

    public abstract AbstractColumnReference column(Consumer<AbstractColumnReference> nestedSelect, String columnAlias);

    public abstract AbstractColumnReference allColumn();

    public abstract AbstractColumnReference allColumn(Class<?> tableClass);

    public abstract AbstractColumnReference allColumn(String tableAlias);

    public abstract <T> TableRelation<T> from(Class<T> tableClass);

    public abstract <T> TableRelation<T> from(Class<T> tableClass, String alias);

    public abstract TableRelation<?> from(CteTable cteTable);

    public abstract TableRelation<?> from(Consumer<AbstractColumnReference> nestedSelect, String selectAlias);

}
