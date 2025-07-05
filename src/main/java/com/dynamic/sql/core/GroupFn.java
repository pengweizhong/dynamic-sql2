/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core;


public class GroupFn implements Fn<Object, Object> {
    private String tableAlias;
    private String columnName;
    private FieldFn<?, ?> fn;

    public GroupFn(String tableAlias, String columnName) {
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    public GroupFn(String tableAlias, FieldFn<?, ?> fn) {
        this.tableAlias = tableAlias;
        this.fn = fn;
    }

    @Override
    public Object resolve(Object o) {
        throw new UnsupportedOperationException();
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public FieldFn<?, ?> getFn() {
        return fn;
    }
}
