package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.column.conventional.AllColumn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.TableRelation;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.NestedColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.function.Consumer;

public class ColumnReference extends AbstractColumnReference {

    public ColumnReference(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn) {
        return this.column(fn, null);
    }

    @Override
    public <T, F> ColumnReference column(String tableAlias, Fn<T, F> fn) {
        return this.column(tableAlias, fn, null);
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn, String columnAlias) {
        return this.column(null, fn, columnAlias);
    }

    @Override
    public <T, F> ColumnReference column(String tableAlias, Fn<T, F> fn, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new Column(tableAlias, fn), null, columnAlias));
        return this;
    }

    @Override
    public ColumnReference column(ColumFunction iColumFunction) {
        column(iColumFunction, null);
        return this;
    }


    @Override
    public AbstractColumnReference column(ColumFunction iColumFunction, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(iColumFunction, null, columnAlias));
        return this;
    }

    @Override
    public AbstractColumnReference column(WindowsFunction windowsFunction, Over over, String columnAlias) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(windowsFunction, over, columnAlias));
        return this;
    }

    @Override
    public AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String columnAlias) {
        if (StringUtils.isBlank(columnAlias)) {
            throw new IllegalArgumentException("Subquery must provide an alias");
        }
        selectSpecification.getColumFunctions().add(new NestedColumn(nestedSelect, columnAlias));
        return this;
    }

    @Override
    public AbstractColumnReference allColumn() {
        return this;
    }

    @Override
    public AbstractColumnReference allColumn(Class<?> tableClass) {
        selectSpecification.getColumFunctions().add(new FunctionColumn(new AllColumn(tableClass), null, null));
        return this;
    }

    @Override
    public AbstractColumnReference allColumn(String tableAlias) {
        return null;
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

}
