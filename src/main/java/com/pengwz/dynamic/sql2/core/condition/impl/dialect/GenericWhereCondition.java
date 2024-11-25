package com.pengwz.dynamic.sql2.core.condition.impl.dialect;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.AggregateFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.FunctionCondition;
import com.pengwz.dynamic.sql2.core.condition.NestedCondition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementSelectWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.LogicalOperatorType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import static com.pengwz.dynamic.sql2.enums.LogicalOperatorType.AND;
import static com.pengwz.dynamic.sql2.enums.LogicalOperatorType.OR;
import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class GenericWhereCondition extends WhereCondition {
    private final Version version;
    private final Map<String, String> aliasTableMap;
    private final StringBuilder condition = new StringBuilder();
    private final ParameterBinder parameterBinder = new ParameterBinder();
    protected String dataSourceName;
    private boolean isFirstBuild;

    public GenericWhereCondition(Version version, Map<String, String> aliasTableMap, String dataSourceName) {
        this.version = version;
        this.aliasTableMap = aliasTableMap;
        this.isFirstBuild = true;
        this.dataSourceName = dataSourceName;
    }

    @Override
    public <T, F> FunctionCondition andEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" = ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public FunctionCondition andEqualTo(Object value, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(registerValueWithKey(parameterBinder, value))
                .append(" = ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" = ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public FunctionCondition orEqualTo(Object value, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(registerValueWithKey(parameterBinder, value))
                .append(" = ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andNotEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" != ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orNotEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" != ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andNotLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orNotLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction, String separator) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction, String separator) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andContains(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orContains(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andAnyIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orAnyIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andAllIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orAllIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andContains(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orContains(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andAnyIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orAnyIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andAllIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orAllIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public NestedCondition andExists(Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND)).append(SqlUtils.getSyntaxExists(matchSqlDialect()));
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
        condition.append(" (").append(sqlStatementWrapper.getRawSql()).append(")");
        parameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public NestedCondition orExists(Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public NestedCondition andNotExists(Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public NestedCondition orNotExists(Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public Condition andCondition(Consumer<Condition> nestedCondition) {
        return nestedCondition(nestedCondition, AND);
    }

    @Override
    public Condition orCondition(Consumer<Condition> nestedCondition) {
        return nestedCondition(nestedCondition, OR);
    }

    private Condition nestedCondition(Consumer<Condition> nestedCondition, LogicalOperatorType logicalOperatorType) {
        MysqlWhereCondition whereCondition = SqlUtils.matchDialectCondition(matchSqlDialect(), version, aliasTableMap, dataSourceName);
        nestedCondition.accept(whereCondition);
        condition.append(" ").append(logicalOperatorType(logicalOperatorType))
                .append(" (").append(whereCondition.getWhereConditionSyntax()).append(") ");
        parameterBinder.addParameterBinder(whereCondition.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> Condition andEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" = ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public Condition andEqualTo(Column column, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.getFunctionToString(sqlDialect(), version);
        String key = SqlUtils.generateBindingKey();
        parameterBinder.add(key, value);
        condition.append(functionToString).append(" = ").append(key);
        return this;
    }

    @Override
    public <T1, T2, F> Condition andEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" = ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }


    @Override
    public <T, F> Condition orEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" = ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" = ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public <T, F> Condition andNotEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" != ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition andNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" != ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public <T, F> Condition orNotEqualTo(Fn<T, F> fn, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" != ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName))
                .append(" != ").append(SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName));
        return this;
    }

    @Override
    public <T, F> Condition andIsNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andIsNotNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNotNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andGreaterThan(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" > ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition andGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orGreaterThan(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" > ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" <= ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andLessThan(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(name)
                .append(" < ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition andLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orLessThan(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andIn(Fn<T, F> fn, Iterable<?> values) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" in (");
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            condition.append(registerValueWithKey(parameterBinder, fn, value));
            if (iterator.hasNext()) {
                condition.append(", ");
            }
        }
        condition.append(")");
        return this;
    }

    @Override
    public <T, F> Condition orIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andNotIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orNotIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return null;
    }

    @Override
    public <T, F> Condition orBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return null;
    }

    @Override
    public <T, F> Condition andNotBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T, F> Condition orNotBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T, F> Condition andLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition orLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition andNotLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition orNotLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition andMatches(Fn<T, F> fn, String regex) {
        return null;
    }

    @Override
    public <T, F> Condition orMatches(Fn<T, F> fn, String regex) {
        return null;
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item) {
        return null;
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        return null;
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item) {
        return null;
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        return null;
    }

    @Override
    public <T, F> Condition andContains(Fn<T, F> fn, String substring) {
        return null;
    }

    @Override
    public <T, F> Condition orContains(Fn<T, F> fn, String substring) {
        return null;
    }

    @Override
    public <T, F> Condition andAnyIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orAnyIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andAllIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orAllIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andIsPositive(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsPositive(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andIsNegative(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNegative(Fn<T, F> fn) {
        return null;
    }

    @Override
    public Condition limit(int offset, int limit) {
        return null;
    }

    @Override
    public Condition limit(int limit) {
        return null;
    }

    @Override
    public Condition customCondition(String customClause, Object... params) {
        return null;
    }

    public String getWhereConditionSyntax() {
        return condition.toString();
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    private String logicalOperatorType(LogicalOperatorType logicalOperatorType) {
        return logicalOperatorType(logicalOperatorType, matchSqlDialect());
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
    public HavingCondition andEqualTo(AggregateFunction function, Object value) {
        String functionToString = executeFunctionToString(function);
        condition.append(" ").append(functionToString).append(" = ").append(registerValueWithKey(parameterBinder, value));
        return this;
    }


    @Override
    public HavingCondition andEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition orEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andNotEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition andNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orNotEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition orNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andGreaterThan(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition andGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orGreaterThan(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition orGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andGreaterThanOrEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition andGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orGreaterThanOrEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition orGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andLessThan(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition andLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orLessThan(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition orLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andLessThanOrEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition andLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orLessThanOrEqualTo(AggregateFunction function, Object value) {
        return null;
    }

    @Override
    public HavingCondition orLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andIn(AggregateFunction function, Iterable<?> values) {
        return null;
    }

    @Override
    public HavingCondition andIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orIn(AggregateFunction function, Iterable<?> values) {
        return null;
    }

    @Override
    public HavingCondition orIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andNotIn(AggregateFunction function, Iterable<?> values) {
        return null;
    }

    @Override
    public HavingCondition andNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition orNotIn(AggregateFunction function, Iterable<?> values) {
        return null;
    }

    @Override
    public HavingCondition orNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        return null;
    }

    @Override
    public HavingCondition andBetween(AggregateFunction function, Object start, Object end) {
        return null;
    }

    @Override
    public HavingCondition orBetween(AggregateFunction function, Object start, Object end) {
        return null;
    }

    @Override
    public HavingCondition andNotBetween(AggregateFunction function, Object start, Object end) {
        return null;
    }

    @Override
    public HavingCondition orNotBetween(AggregateFunction function, Object start, Object end) {
        return null;
    }

    @Override
    public HavingCondition andIsPositive(AggregateFunction function) {
        return null;
    }

    @Override
    public HavingCondition orIsPositive(AggregateFunction function) {
        return null;
    }

    @Override
    public HavingCondition andIsNegative(AggregateFunction function) {
        return null;
    }

    @Override
    public HavingCondition orIsNegative(AggregateFunction function) {
        return null;
    }

    protected String executeFunctionToString(AggregateFunction function) {
        return function.getFunctionToString(matchSqlDialect(), version);
    }

    private SqlDialect matchSqlDialect() {
        if (this instanceof MysqlWhereCondition) {
            return SqlDialect.MYSQL;
        }
        if (this instanceof OracleWhereCondition) {
            return SqlDialect.ORACLE;
        }
        return SqlDialect.MYSQL;
    }

    protected SqlDialect sqlDialect() {
        throw new UnsupportedOperationException("Unimplemented SQL dialects");
    }
}
