package com.pengwz.dynamic.sql2.core.dml.select.order;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.enums.SortOrder;

public class DefaultOrderBy extends OrderBy {
    private FieldFn fn;
    private String tableAlias;
    private String columnName;

    public <T, F> DefaultOrderBy(FieldFn<T, F> fn, SortOrder sortOrder) {
        super(sortOrder);
        this.fn = fn;
    }

    public <T, F> DefaultOrderBy(String tableAlias, FieldFn<T, F> fn, SortOrder sortOrder) {
        super(sortOrder);
        this.fn = fn;
        this.tableAlias = tableAlias;
    }

    public DefaultOrderBy(String tableAlias, String columnName, SortOrder sortOrder) {
        super(sortOrder);
        this.columnName = columnName;
        this.tableAlias = tableAlias;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public FieldFn getFieldFn() {
        return fn;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}
