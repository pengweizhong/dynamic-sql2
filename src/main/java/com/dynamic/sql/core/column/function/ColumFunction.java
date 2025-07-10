/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

/**
 * 一般情况下实现者不能直接实现该类，需要先继承AbstractColumFunction，因为AbstractColumFunction附带列运算
 */
public interface ColumFunction {

    String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException;

    Fn<?, ?> getOriginColumnFn();

    ParameterBinder getParameterBinder();

    String getTableAlias();

    default void setTableAlias(String tableAlias) {
    }
}
