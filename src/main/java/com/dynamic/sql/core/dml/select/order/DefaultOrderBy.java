package com.dynamic.sql.core.dml.select.order;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.enums.SortOrder;

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
