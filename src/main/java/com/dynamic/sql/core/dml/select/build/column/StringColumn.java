/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build.column;


import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.windows.Over;

import java.util.function.Consumer;

public class StringColumn implements ColumnQuery {
    //原始SQL
    private String sql;

    public StringColumn(String sql) {
        this.sql = sql;
    }

    @Override
    public String getAlias() {
        throw new UnsupportedOperationException();
    }

    public ColumFunction getColumFunction() {
        throw new UnsupportedOperationException();
    }

    public Consumer<Over> getOver() {
        throw new UnsupportedOperationException();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
