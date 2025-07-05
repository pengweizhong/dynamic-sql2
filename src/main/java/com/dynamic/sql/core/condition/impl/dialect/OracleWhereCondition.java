/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.condition.impl.dialect;

import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.enums.LogicalOperatorType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.utils.SqlUtils;

import java.util.Map;

import static com.dynamic.sql.enums.LogicalOperatorType.AND;
import static com.dynamic.sql.enums.LogicalOperatorType.OR;
import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public class OracleWhereCondition extends GenericWhereCondition {

    public OracleWhereCondition(Version version, Map<String, TableAliasMapping> aliasTableMap, String dataSourceName) {
        super(version, aliasTableMap, dataSourceName);
    }

    @Override
    protected SqlDialect sqlDialect() {
        return SqlDialect.ORACLE;
    }

    @Override
    public <T, F> GenericWhereCondition andMatches(Fn<T, F> fn, String regex) {
        return matches(AND, fn, regex);
    }

    @Override
    public <T, F> GenericWhereCondition orMatches(Fn<T, F> fn, String regex) {
        return matches(OR, fn, regex);
    }

    public <T, F> GenericWhereCondition matches(LogicalOperatorType logicalOperatorType, Fn<T, F> fn, String regex) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType);
        condition.append(" REGEXP_LIKE(").append(column).append(", ")
                .append(registerValueWithKey(parameterBinder, fn, regex)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return matchesFunction(AND, fn, columFunction);
    }

    @Override
    public <T, F> GenericWhereCondition orMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return matchesFunction(OR, fn, columFunction);
    }

    public <T, F> GenericWhereCondition matchesFunction(LogicalOperatorType logicalOperatorType, Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType);
        condition.append(" REGEXP_LIKE(").append(column).append(", ")
                .append(registerValueWithKey(parameterBinder, fn, columFunction.getFunctionToString(sqlDialect(), version)))
                .append(")");
        return this;
    }
}
