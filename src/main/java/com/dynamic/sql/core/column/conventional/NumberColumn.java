/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.conventional;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;
import com.dynamic.sql.core.placeholder.ParameterBinder;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public final class NumberColumn implements ColumFunction, ColumnQuery {

    private int numberColumn;

    public NumberColumn(int num) {
        this.numberColumn = num;
    }

    @Override
    public String getTableAlias() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAlias() {
        return getTableAlias();
    }

    @Override
    public Fn<?, ?> originColumn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterBinder parameterBinder() {
        ParameterBinder parameterBinder = new ParameterBinder();
        registerValueWithKey(parameterBinder, numberColumn);
        return parameterBinder;
    }

    @Override
    public String render(RenderContext context) {
        return numberColumn + "";
    }
}
