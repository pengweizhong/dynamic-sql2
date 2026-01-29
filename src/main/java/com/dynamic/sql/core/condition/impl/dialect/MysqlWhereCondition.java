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
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.LogicalOperatorType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.utils.SqlUtils;

import java.util.Map;

import static com.dynamic.sql.enums.LogicalOperatorType.AND;
import static com.dynamic.sql.enums.LogicalOperatorType.OR;
import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public class MysqlWhereCondition extends GenericWhereCondition {

    public MysqlWhereCondition(Version version, Map<String, TableAliasMapping> aliasTableMap, String dataSourceName) {
        super(version, aliasTableMap, dataSourceName);
    }

    @Override
    protected SqlDialect sqlDialect() {
        return SqlDialect.MYSQL;
    }

    @Override
    public <T, F> GenericWhereCondition andMatches(Fn<T, F> fn, String regex) {
        return matches(AND, fn, regex);
    }

    @Override
    public GenericWhereCondition andMatches(Column column, String value) {
        return matches(AND, column, value);
    }

    @Override
    public <T, F> GenericWhereCondition orMatches(Fn<T, F> fn, String regex) {
        return matches(OR, fn, regex);
    }

    @Override
    public GenericWhereCondition orMatches(Column column, String value) {
        return matches(OR, column, value);
    }

    public <T, F> GenericWhereCondition matches(LogicalOperatorType logicalOperatorType, Fn<T, F> fn, String regex) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        String key = registerValueWithKey(parameterBinder, fn, regex, sqlDialect());
        condition.append(" ").append(logicalOperatorType(logicalOperatorType));
        //使用 REGEXP_LIKE (MySQL 8.0+)
        if (version.isGreaterThanOrEqual(new Version(8, 0, 0))) {
            condition.append(" regexp_like(").append(column).append(", ")
                    .append(key).append(")");
            return this;
        }
        //使用 REGEXP (MySQL 5.7+)
        condition.append(" ").append(column).append(" regexp ").append(key);
        return this;
    }

    public GenericWhereCondition matches(LogicalOperatorType logicalOperatorType, Column column, String regex) {
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, regex);
        condition.append(" ").append(logicalOperatorType(logicalOperatorType));
        //使用 REGEXP_LIKE (MySQL 8.0+)
        if (version.isGreaterThanOrEqual(new Version(8, 0, 0))) {
            condition.append(" regexp_like(").append(functionToString).append(", ")
                    .append(key).append(")");
            return this;
        }
        //使用 REGEXP (MySQL 5.7+)
        condition.append(" ").append(functionToString).append(" regexp ").append(key);
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
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(logicalOperatorType));
        //使用 REGEXP_LIKE (MySQL 8.0+)
        if (version.isGreaterThanOrEqual(new Version(8, 0, 0))) {
            condition.append(" regexp_like(").append(column).append(", ")
                    .append(registerValueWithKey(parameterBinder, fn, columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)), sqlDialect()))
                    .append(")");
            return this;
        }
        //使用 REGEXP (MySQL 5.7+)
        condition.append(" ").append(column).append(" regexp ").append(registerValueWithKey(parameterBinder, fn,
                columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)), sqlDialect()));

        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, Object item) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item, sqlDialect()))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition andFindInSet(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(" find_in_set(").append(key)
                .append(", ").append(functionToString).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        condition.append(" ").append(logicalOperatorType(AND));
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        //REPLACE(str, from_str, to_str)
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item, sqlDialect()))
                .append(", ").append("replace(").append(column).append(", ").append(registerValueWithKey(parameterBinder, null, separator, sqlDialect()))
                .append(", ',')").append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, Object item) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item, sqlDialect()))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition orFindInSet(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(" find_in_set(").append(key)
                .append(", ").append(functionToString).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        condition.append(" ").append(logicalOperatorType(OR));
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        //REPLACE(str, from_str, to_str)
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item, sqlDialect()))
                .append(", ").append("replace(").append(column).append(", ").append(registerValueWithKey(parameterBinder, null, separator, sqlDialect()))
                .append(", ',')").append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn,
                        columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)), sqlDialect()))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn,
                        columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)), sqlDialect()))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition limit(int limit) {
        return limit(0, limit);
    }

    @Override
    public GenericWhereCondition limit(int offset, int limit) {
        condition.append(" limit ").append(registerValueWithKey(parameterBinder, null, offset, sqlDialect()))
                .append(", ").append(registerValueWithKey(parameterBinder, null, limit, sqlDialect()));
        return this;
    }

}
