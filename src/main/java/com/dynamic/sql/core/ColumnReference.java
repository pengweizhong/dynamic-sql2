package com.dynamic.sql.core;


import com.dynamic.sql.core.column.conventional.AllColumn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.modifiers.Distinct;
import com.dynamic.sql.core.column.function.windows.Over;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.core.dml.select.TableRelation;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;
import com.dynamic.sql.core.dml.select.build.column.FunctionColumn;
import com.dynamic.sql.core.dml.select.build.column.NestedColumn;
import com.dynamic.sql.core.dml.select.build.join.FromJoin;
import com.dynamic.sql.core.dml.select.build.join.FromNestedJoin;
import com.dynamic.sql.core.dml.select.build.join.NestedJoin;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.utils.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class ColumnReference extends AbstractColumnReference {

    public ColumnReference(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    @Override
    public AbstractColumnReference distinct() {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new Distinct(), null, null));
        return this;
    }

    /**
     * 构建一个查询列的引用集合，用于定义查询中的多个列。
     * <p>
     * 此方法创建并返回一个 `AbstractColumnReference` 实例，
     * 允许通过链式调用添加列或嵌套的列引用。
     * <p>
     * 示例：
     * <pre>
     * AbstractColumnReference columnReference = ColumnReference.withColumns()
     *         .column(Product::getProductId)
     *         .column(Product::getProductName)
     *         .columnReference(nestedColumnReference());
     * </pre>
     *
     * @return 一个用于构建列集合的 {@link AbstractColumnReference} 对象
     */
    public static AbstractColumnReference withColumns() {
        return new ColumnReference(new SelectSpecification());
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new Column(null, fn), null, null));
        return this;
    }

    @Override
    public <T, F> ColumnReference column(String tableAlias, FieldFn<T, F> fn) {
        return this.column(tableAlias, fn, null);
    }

    @Override
    public <T, F> ColumnReference column(FieldFn<T, F> fn, String columnAlias) {
        return this.column(null, fn, columnAlias);
    }

    @Override
    public <T, F> ColumnReference column(String tableAlias, FieldFn<T, F> fn, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new Column(tableAlias, fn), null, columnAlias));
        return this;
    }

    @Override
    public AbstractColumnReference column(String tableAlias, String columnName) {
        return column(tableAlias, columnName, null);
    }

    @Override
    public AbstractColumnReference column(String tableAlias, String columnName, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new Column(tableAlias, columnName), null, columnAlias));
        return this;
    }

    @Override
    public ColumnReference column(AbstractColumFunction iColumFunction) {
        column(iColumFunction, null);
        return this;
    }

    @Override
    public AbstractColumnReference column(AbstractColumFunction iColumFunction, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(iColumFunction, null, columnAlias));
        return this;
    }

    @Override
    public AbstractColumnReference column(WindowsFunction windowsFunction, Consumer<Over> over, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(windowsFunction, over, columnAlias));
        return this;
    }

    @Override
    public AbstractColumnReference column(Consumer<AbstractColumnReference> nestedSelect, String columnAlias) {
        if (StringUtils.isBlank(columnAlias)) {
            throw new IllegalArgumentException("Subquery must provide an alias");
        }
        NestedColumn nestedColumn = new NestedColumn(nestedSelect, columnAlias);
        selectSpecification.getColumFunctions().add(nestedColumn);
        return this;
    }

    @Override
    @Deprecated
    public AbstractColumnReference columnReference(AbstractColumnReference columnReference) {
        List<ColumnQuery> columFunctions = columnReference.getSelectSpecification().getColumFunctions();
        selectSpecification.getColumFunctions().addAll(columFunctions);
        return this;
    }

    @Override
    public AbstractColumnReference allColumn() {
        return allColumn((Class<?>) null);
    }

    @Override
    public AbstractColumnReference allColumn(Class<?> tableClass) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new AllColumn(tableClass), null, null));
        return this;
    }

    @Override
    public AbstractColumnReference allColumn(String tableAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new AllColumn(tableAlias, null), null, null));
        return this;
    }

    @Override
    public <T> TableRelation<T> from(Class<T> tableClass) {
        return from(tableClass, null);
    }

    @Override
    public <T> TableRelation<T> from(Class<T> tableClass, String alias) {
        selectSpecification.getJoinTables().add(new FromJoin(tableClass, alias));
        return new TableRelation<>(selectSpecification);
    }

    @Override
    public TableRelation<?> from(CteTable cteTable) {
        selectSpecification.getJoinTables().add(new FromJoin(cteTable));
        return new TableRelation<>(selectSpecification);
    }

    @Override
    public TableRelation<?> from(Consumer<AbstractColumnReference> nestedSelect, String selectAlias) {
        selectSpecification.getJoinTables().add(new FromNestedJoin(new NestedJoin(nestedSelect, selectAlias)));
        return new TableRelation<>(selectSpecification);
    }
}
