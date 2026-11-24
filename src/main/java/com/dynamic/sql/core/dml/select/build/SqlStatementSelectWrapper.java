/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build;


import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;

public class SqlStatementSelectWrapper extends SqlStatementWrapper {

    private Class<?> guessTheTargetClass;

    public SqlStatementSelectWrapper(String dataSourceName, StringBuilder rawSql,
                                     ParameterBinder parameterBinder, Class<?> guessTheTargetClass) {
        super(dataSourceName, rawSql, parameterBinder);
        this.guessTheTargetClass = guessTheTargetClass;
    }

    public Class<?> getGuessTheTargetClass() {
        return guessTheTargetClass;
    }
}
