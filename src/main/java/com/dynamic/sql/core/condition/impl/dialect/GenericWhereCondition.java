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
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.dml.select.NestedMeta;
import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.LogicalOperatorType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.utils.SqlUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import static com.dynamic.sql.enums.LogicalOperatorType.AND;
import static com.dynamic.sql.enums.LogicalOperatorType.OR;
import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;

public class GenericWhereCondition extends WhereCondition<GenericWhereCondition> {
    protected final Version version;
    /**
     * TODO 把这个抽成一个类，用来管理别名和表的映射关系
     * 别名-表映射
     * 1、** 表示如果没有别名则使用该表
     * 2、*** 表示如果有没有别名都强制使用该表
     */
    protected final Map<String, TableAliasMapping> aliasTableMap;
    protected final StringBuilder condition = new StringBuilder();
    protected final ParameterBinder parameterBinder = new ParameterBinder();
    protected String dataSourceName;
    protected boolean isFirstBuild;
    protected SqlExecuteType sqlExecuteType;

    public GenericWhereCondition(Version version, Map<String, TableAliasMapping> aliasTableMap, String dataSourceName) {
        this.version = version;
        this.aliasTableMap = aliasTableMap;
        this.isFirstBuild = true;
        this.dataSourceName = dataSourceName;
    }

    @Override
    public <T, F> GenericWhereCondition andEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" = ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public GenericWhereCondition andEqualTo(Object value, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(registerValueWithKey(parameterBinder, value))
                .append(" = ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" = ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public GenericWhereCondition orEqualTo(Object value, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(registerValueWithKey(parameterBinder, value))
                .append(" = ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" != ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" != ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" > ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" > ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" >= ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" >= ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" < ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" < ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" <= ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" <= ").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap)));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" in (").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap))).append(")");
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" in (").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap))).append(")");
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" not in (").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap))).append(")");
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" not in (").append(columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap))).append(")");
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andMatches(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition orMatches(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    private StringBuilder nestedSelectSql(SelectDsl nestedSelect) {
        NestedMeta nestedMeta = new NestedMeta();
        nestedMeta.setSqlDialect(sqlDialect());
        nestedMeta.setVersion(version);
        nestedMeta.setDataSourceName(dataSourceName);
        SqlStatementSelectWrapper sqlStatementSelectWrapper = SqlUtils.executeNestedSelect(nestedMeta, nestedSelect, aliasTableMap);
        parameterBinder.addParameterBinder(sqlStatementSelectWrapper.getParameterBinder());
        return sqlStatementSelectWrapper.getRawSql();
    }

    @Override
    public <T, F> GenericWhereCondition andEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" = (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" = (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" != (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" != (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andGreaterThan(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" > (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orGreaterThan(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" > (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andGreaterThanOrEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" >= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orGreaterThanOrEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" >= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLessThan(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" < (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLessThan(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" < (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLessThanOrEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" <= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLessThanOrEqualTo(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" <= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andIn(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orIn(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotIn(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotIn(Fn<T, F> fn, SelectDsl nestedSelect) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition andExists(SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(SqlUtils.getSyntaxExists(sqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition orExists(SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(SqlUtils.getSyntaxExists(sqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition andNotExists(SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND)).append(" not ")
                .append(SqlUtils.getSyntaxExists(sqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition orNotExists(SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR)).append(" not ")
                .append(SqlUtils.getSyntaxExists(sqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition andCondition(Consumer<GenericWhereCondition> nestedCondition) {
        return nestedCondition(nestedCondition, AND);
    }

    @Override
    public GenericWhereCondition orCondition(Consumer<GenericWhereCondition> nestedCondition) {
        return nestedCondition(nestedCondition, OR);
    }

    @Override
    public GenericWhereCondition andFunction(ColumFunction columFunction) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString);
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    @Override
    public GenericWhereCondition orFunction(ColumFunction columFunction) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString);
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        return this;
    }

    private GenericWhereCondition nestedCondition(Consumer<GenericWhereCondition> nestedCondition, LogicalOperatorType logicalOperatorType) {
        MysqlWhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect(), version, aliasTableMap, dataSourceName);
        whereCondition.setSqlExecuteType(this.sqlExecuteType);
        nestedCondition.accept(whereCondition);
        condition.append(" ").append(logicalOperatorType(logicalOperatorType))
                .append(" (").append(whereCondition.getWhereConditionSyntax()).append(") ");
        parameterBinder.addParameterBinder(whereCondition.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" = ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andEqualTo(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" = ").append(key);
        return this;
    }

    @Override
    public GenericWhereCondition andEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" = ").append(functionToString2);
        return this;
    }


    @Override
    public <T1, T2, F> GenericWhereCondition andEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" = ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public GenericWhereCondition andEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" = ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }


    @Override
    public <T, F> GenericWhereCondition orEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" = ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orEqualTo(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" = ").append(key);
        return this;
    }

    @Override
    public GenericWhereCondition orEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" = ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" = ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public GenericWhereCondition orEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" = ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" != ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andNotEqualTo(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" != ").append(key);
        return this;
    }

    @Override
    public GenericWhereCondition andNotEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" != ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition andNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" != ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public GenericWhereCondition andNotEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" != ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" != ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orNotEqualTo(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" != ").append(key);
        return this;
    }

    @Override
    public GenericWhereCondition orNotEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" != ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" != ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public GenericWhereCondition orNotEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" != ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andIsNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" is null");
        return this;
    }

    @Override
    public GenericWhereCondition andIsNull(Column column) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" is null");
        return this;
    }

    @Override
    public GenericWhereCondition andIsNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        registerValueWithKey(parameterBinder, null, value, sqlDialect());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" is null");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orIsNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" is null");
        return this;
    }

    @Override
    public GenericWhereCondition orIsNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        registerValueWithKey(parameterBinder, null, value, sqlDialect());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" is null");
        return this;
    }

    @Override
    public GenericWhereCondition orIsNull(Column column) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" is null");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andIsNotNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" is not null");
        return this;
    }

    @Override
    public GenericWhereCondition andIsNotNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        registerValueWithKey(parameterBinder, null, value, sqlDialect());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" is not null");
        return this;
    }

    @Override
    public GenericWhereCondition andIsNotNull(Column column) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" is not null");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orIsNotNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(extractQualifiedAliasToColumn(fn))
                .append(" is not null");
        return this;
    }

    @Override
    public GenericWhereCondition orIsNotNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        registerValueWithKey(parameterBinder, null, value, sqlDialect());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" is not null");
        return this;
    }

    @Override
    public GenericWhereCondition orIsNotNull(Column column) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" is not null");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andGreaterThan(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" > ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThan(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" > ").append(key);
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThan(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" > ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition andGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" > ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" > ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orGreaterThan(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" > ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThan(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" > ").append(key);
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThan(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" > ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" > ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" > ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(name)
                .append(" >= ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThanOrEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" >= ").append(functionToString2);
        return this;
    }


    @Override
    public <T1, T2, F> GenericWhereCondition andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" >= ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" >= ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" >= ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThanOrEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" >= ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" >= ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" >= ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLessThan(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(name)
                .append(" < ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andLessThan(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" < ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition andLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition andLessThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" < ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLessThan(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" < ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orLessThan(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" < ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition orLessThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" < ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(name)
                .append(" <= ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andLessThanOrEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" <= ").append(functionToString2);
        return this;
    }

    @Override
    public GenericWhereCondition andLessThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" <= ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = extractQualifiedAliasToColumn(field1);
        String name2 = extractQualifiedAliasToColumn(field2);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" <= ").append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orLessThanOrEqualTo(Column column, Column column2) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String functionToString2 = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" <= ").append(functionToString2);
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName, sqlExecuteType);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName, sqlExecuteType);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public GenericWhereCondition orLessThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" <= ").append(registerValueWithKey(parameterBinder, null, value, sqlDialect()));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andIn(Fn<T, F> fn, Iterable<?> values) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition andIn(Column column, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString).append(" in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public GenericWhereCondition andIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(functionToString).append(" in");
        iteratingCollection(values);
        return this;
    }


    @Override
    public <T, F> GenericWhereCondition orIn(Fn<T, F> fn, Iterable<?> values) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition orIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(functionToString).append(" in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotIn(Fn<T, F> fn, Iterable<?> values) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition andNotIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(functionToString).append(" not in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotIn(Fn<T, F> fn, Iterable<?> values) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition orNotIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        parameterBinder.addParameterBinder(columFunction.parameterBinder());
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(functionToString).append(" not in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andBetween(Fn<T, F> fn, Object start, Object end) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" between ")
                .append(registerValueWithKey(parameterBinder, fn, start, sqlDialect()))
                .append(" and ")
                .append(registerValueWithKey(parameterBinder, fn, end, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andBetween(Column column, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString)
                .append(" between ")
                .append(registerValueWithKey(parameterBinder, start))
                .append(" and ")
                .append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        String column = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName, sqlExecuteType);
        String column2 = SqlUtils.extractQualifiedAlias(startField, aliasTableMap, dataSourceName, sqlExecuteType);
        String column3 = SqlUtils.extractQualifiedAlias(endField, aliasTableMap, dataSourceName, sqlExecuteType);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" between ").append(column2)
                .append(" and ").append(column3);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orBetween(Fn<T, F> fn, Object start, Object end) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" between ").append(registerValueWithKey(parameterBinder, fn, start, sqlDialect()))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orBetween(Column column, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString)
                .append(" between ")
                .append(registerValueWithKey(parameterBinder, start))
                .append(" and ")
                .append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public <T1, T2, F> GenericWhereCondition orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        String column = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName, sqlExecuteType);
        String column2 = SqlUtils.extractQualifiedAlias(startField, aliasTableMap, dataSourceName, sqlExecuteType);
        String column3 = SqlUtils.extractQualifiedAlias(endField, aliasTableMap, dataSourceName, sqlExecuteType);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" between ").append(column2)
                .append(" and ").append(column3);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotBetween(Fn<T, F> fn, Object start, Object end) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not between ").append(registerValueWithKey(parameterBinder, fn, start, sqlDialect()))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andNotBetween(Column column, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString)
                .append(" not between ")
                .append(registerValueWithKey(parameterBinder, start))
                .append(" and ")
                .append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotBetween(Fn<T, F> fn, Object start, Object end) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not between ").append(registerValueWithKey(parameterBinder, fn, start, sqlDialect()))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orNotBetween(Column column, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        condition.append(functionToString)
                .append(" not between ")
                .append(registerValueWithKey(parameterBinder, start))
                .append(" and ")
                .append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andLike(Fn<T, F> fn, String pattern) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" like ").append(registerValueWithKey(parameterBinder, fn, pattern, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andLike(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" like ").append(key);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orLike(Fn<T, F> fn, String pattern) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" like ").append(registerValueWithKey(parameterBinder, fn, pattern, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orLike(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" like ").append(key);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andNotLike(Fn<T, F> fn, String pattern) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not like ").append(registerValueWithKey(parameterBinder, fn, pattern, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition andNotLike(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" not like ").append(key);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orNotLike(Fn<T, F> fn, String pattern) {
        String column = extractQualifiedAliasToColumn(fn);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not like ").append(registerValueWithKey(parameterBinder, fn, pattern, sqlDialect()));
        return this;
    }

    @Override
    public GenericWhereCondition orNotLike(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        String functionToString = column.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
        String key = registerValueWithKey(parameterBinder, value);
        condition.append(functionToString).append(" not like ").append(key);
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andMatches(Fn<T, F> fn, String regex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenericWhereCondition andMatches(Column column, String regex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition orMatches(Fn<T, F> fn, String regex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenericWhereCondition orMatches(Column column, String regex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, Object item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenericWhereCondition andFindInSet(Column column, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, Object item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenericWhereCondition orFindInSet(Column column, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenericWhereCondition limit(int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenericWhereCondition limit(int limit) {
        throw new UnsupportedOperationException();
    }

    public String getWhereConditionSyntax() {
        return condition.toString();
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    protected String logicalOperatorType(LogicalOperatorType logicalOperatorType) {
        return logicalOperatorType(logicalOperatorType, sqlDialect());
    }

    /**
     * 首次连接时才会去拼接 and  或者 or
     */
    protected String logicalOperatorType(LogicalOperatorType logicalOperatorType, SqlDialect sqlDialect) {
        if (isFirstBuild) {
            isFirstBuild = false;
            return "";
        }
        return SqlUtils.getSyntaxLogicalOperator(logicalOperatorType, sqlDialect) + " ";
    }

    @Override
    public GenericWhereCondition andEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }


    @Override
    public GenericWhereCondition andEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition orEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition orEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition andNotEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition andNotEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition orNotEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition orNotEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThan(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThan(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition andGreaterThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition orGreaterThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition andLessThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition andLessThan(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition orLessThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition orLessThan(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition andLessThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition andLessThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition orLessThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public GenericWhereCondition orLessThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public GenericWhereCondition andIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition andIn(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition orIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition orIn(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition andNotIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" not in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition andNotIn(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition orNotIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" not in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public GenericWhereCondition orNotIn(AggregateFunction function, SelectDsl nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition andBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public GenericWhereCondition orBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public GenericWhereCondition andNotBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" not between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public GenericWhereCondition orNotBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" not between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public GenericWhereCondition sql(String sql) {
        condition.append(" ").append(sql);
        return this;
    }

    protected String executeFunctionToString(AggregateFunction function) {
        return function.render(new RenderContext(dataSourceName, sqlDialect(), version, aliasTableMap));
    }

    private void iteratingCollection(Iterable<?> values) {
        condition.append(" (");
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            condition.append(registerValueWithKey(parameterBinder, value));
            if (iterator.hasNext()) {
                condition.append(", ");
            }
        }
        condition.append(")");
    }

    private <T, F> void iteratingFnCollection(Fn<T, F> fn, Iterator<?> iterator) {
        if (iterator == null || !iterator.hasNext()) {
            throw new IllegalArgumentException("The parameter set cannot be empty");
        }
        condition.append(" (");
        while (iterator.hasNext()) {
            Object value = iterator.next();
            condition.append(registerValueWithKey(parameterBinder, fn, value, sqlDialect()));
            if (iterator.hasNext()) {
                condition.append(", ");
            }
        }
        condition.append(")");
    }

    protected SqlDialect sqlDialect() {
        throw new UnsupportedOperationException("Unimplemented SQL dialects");
    }

    public void setSqlExecuteType(SqlExecuteType sqlExecuteType) {
        this.sqlExecuteType = sqlExecuteType;
    }

    <T, F> String extractQualifiedAliasToColumn(Fn<T, F> fn) {
        return SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName, sqlExecuteType);
    }
}
