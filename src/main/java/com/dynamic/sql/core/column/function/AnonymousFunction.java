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
 * 匿名函数，当明确不需要函数调用时使用，此类存在的目的是为了统一函数调用行为
 */
public class AnonymousFunction extends AbstractColumFunction {

    public AnonymousFunction() {
    }

    /**
     * 仅仅记录函数，不做任何特殊处理
     */
    public AnonymousFunction(String functionToString, ParameterBinder parameterBinder) {
//        arithmeticSql.append(functionToString);
//        arithmeticParameterBinder.addParameterBinder(parameterBinder);
    }

    public String getFunctionToString() {
//        return arithmeticSql.toString();
        return "ANONYMOUS";
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        return "";
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return null;
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return "";
    }
}
