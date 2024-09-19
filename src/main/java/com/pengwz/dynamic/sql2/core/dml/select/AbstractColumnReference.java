package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 该抽象类提供了 SQL 查询列的引用及其相关操作的基础实现。
 * 包括选择特定列、应用函数、使用别名等。
 */
public abstract class AbstractColumnReference {
    protected List<ColumFunction> queryFields = new ArrayList<>();

    //简简单单的  select 1 from ...
    public abstract AbstractColumnReference one();

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn);

    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn, String alias);

    public abstract AbstractColumnReference column(ColumFunction iColumFunction);

    public abstract AbstractColumnReference column(ColumFunction iColumFunction, String alias);

    public abstract AbstractColumnReference column(WindowsFunction windowsFunction, Over over, String alias);

    public abstract AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String alias);

    public AbstractColumnReference allColumn() {
        return new AllColumnReference();
    }

    //TODO 多表时需要指定取哪个表的全部列，发生字段冲突时以谁的为准
    public AbstractColumnReference allColumn(Class<?> tableClass/*, merge...*/) {
        return new AllColumnReference();
    }

    public abstract <T> TableRelation<T> from(Class<T> tableClass);

    public abstract TableRelation<?> from(CteTable cteTable);

    public static class AllColumnReference extends ColumnReference {
        public AllColumnReference() {
            queryFields.add(new Column("*"));
        }
    }
}
