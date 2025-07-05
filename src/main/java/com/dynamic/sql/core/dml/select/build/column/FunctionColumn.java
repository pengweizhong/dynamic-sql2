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

public class FunctionColumn implements ColumnQuery {
    //列函数
    private ColumFunction columFunction;
    //滑窗函数
    private Consumer<Over> over;
    //
    private String alias;

    public FunctionColumn(ColumFunction columFunction, Consumer<Over> over, String alias) {
        this.columFunction = columFunction;
        this.over = over;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public ColumFunction getColumFunction() {
        return columFunction;
    }

    public Consumer<Over> getOver() {
        return over;
    }

}
