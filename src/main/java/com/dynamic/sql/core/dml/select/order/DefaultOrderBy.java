/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.order;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.enums.SortOrder;

public class DefaultOrderBy extends OrderBy {
    private FieldFn fn;
    private ColumFunction columFunction;
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

    public DefaultOrderBy(ColumFunction columFunction, SortOrder sortOrder) {
        super(sortOrder);
        this.columFunction = columFunction;
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

    public ColumFunction getColumFunction() {
        return columFunction;
    }
}
