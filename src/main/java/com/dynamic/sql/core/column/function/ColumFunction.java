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


/**
 * 一般情况下实现者不能直接实现该类，需要先继承AbstractColumFunction，因为AbstractColumFunction附带列运算
 */
public interface ColumFunction extends SqlRenderable, Bindable, TableFunction {

//    String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException;
//
//    Fn<?, ?> getOriginColumnFn();
//
//    ParameterBinder getParameterBinder();

    String getTableAlias();

    default void setTableAlias(String tableAlias) {
    }
}
