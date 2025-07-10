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
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public final class AllColumn implements ColumFunction {

    private final Class<?> tableClass;
    private String tableAlias;

    public AllColumn(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public AllColumn(String tableAlias, Class<?> tableClass) {
        this.tableClass = tableClass;
        this.tableAlias = tableAlias;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }
}
