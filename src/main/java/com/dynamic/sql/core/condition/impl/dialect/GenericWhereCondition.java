package com.dynamic.sql.core.condition.impl.dialect;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;
import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.condition.FunctionCondition;
import com.dynamic.sql.core.condition.NestedCondition;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.dml.select.HavingCondition;
import com.dynamic.sql.core.dml.select.NestedMeta;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.LogicalOperatorType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import static com.dynamic.sql.enums.LogicalOperatorType.AND;
import static com.dynamic.sql.enums.LogicalOperatorType.OR;
import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;

public class GenericWhereCondition extends WhereCondition {
    protected final Version version;
    protected final Map<String, String> aliasTableMap;
    protected final StringBuilder condition = new StringBuilder();
    protected final ParameterBinder parameterBinder = new ParameterBinder();
    protected String dataSourceName;
    protected boolean isFirstBuild;

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
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" > ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" > ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" >= ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" >= ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" < ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" < ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" <= ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" <= ").append(columFunction.getFunctionToString(sqlDialect(), version));
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" in (").append(columFunction.getFunctionToString(sqlDialect(), version)).append(")");
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" in (").append(columFunction.getFunctionToString(sqlDialect(), version)).append(")");
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" not in (").append(columFunction.getFunctionToString(sqlDialect(), version)).append(")");
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition orNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" not in (").append(columFunction.getFunctionToString(sqlDialect(), version)).append(")");
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> FunctionCondition andMatches(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> FunctionCondition orMatches(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        throw new UnsupportedOperationException();
    }

    private StringBuilder nestedSelectSql(Consumer<AbstractColumnReference> nestedSelect) {
        NestedMeta nestedMeta = new NestedMeta();
        nestedMeta.setSqlDialect(sqlDialect());
        nestedMeta.setVersion(version);
        nestedMeta.setDataSourceName(dataSourceName);
        SqlStatementSelectWrapper sqlStatementSelectWrapper = SqlUtils.executeNestedSelect(nestedMeta, nestedSelect);
        parameterBinder.addParameterBinder(sqlStatementSelectWrapper.getParameterBinder());
        return sqlStatementSelectWrapper.getRawSql();
    }

    @Override
    public <T, F> NestedCondition andEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" = (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" = (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" != (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" != (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" > (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" > (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" >= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" >= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" < (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" < (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" <= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" <= (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition andNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public <T, F> NestedCondition orNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public NestedCondition andExists(Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(SqlUtils.getSyntaxExists(matchSqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public NestedCondition orExists(Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(SqlUtils.getSyntaxExists(matchSqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public NestedCondition andNotExists(Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND)).append(" not ")
                .append(SqlUtils.getSyntaxExists(matchSqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public NestedCondition orNotExists(Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR)).append(" not ")
                .append(SqlUtils.getSyntaxExists(matchSqlDialect()))
                .append(" (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public Condition andCondition(Consumer<Condition> nestedCondition) {
        return nestedCondition(nestedCondition, AND);
    }

    @Override
    public Condition orCondition(Consumer<Condition> nestedCondition) {
        return nestedCondition(nestedCondition, OR);
    }

    @Override
    public Condition andFunction(ColumFunction columFunction) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
    }

    @Override
    public Condition orFunction(ColumFunction columFunction) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        return this;
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
        String key = registerValueWithKey(parameterBinder, value);
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
    public Condition andEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" = ").append(registerValueWithKey(parameterBinder, null, value));
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
    public Condition orEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" = ").append(registerValueWithKey(parameterBinder, null, value));
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
    public Condition andNotEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" != ").append(registerValueWithKey(parameterBinder, null, value));
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
    public Condition orNotEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" != ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T, F> Condition andIsNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" is null");
        return this;
    }

    @Override
    public Condition andIsNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        registerValueWithKey(parameterBinder, null, value);
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" is null");
        return this;
    }

    @Override
    public <T, F> Condition orIsNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" is null");
        return this;
    }

    @Override
    public Condition orIsNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        registerValueWithKey(parameterBinder, null, value);
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" is null");
        return this;
    }

    @Override
    public <T, F> Condition andIsNotNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" is not null");
        return this;
    }

    @Override
    public Condition andIsNotNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        registerValueWithKey(parameterBinder, null, value);
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" is not null");
        return this;
    }

    @Override
    public <T, F> Condition orIsNotNull(Fn<T, F> fn) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName))
                .append(" is not null");
        return this;
    }

    @Override
    public Condition orIsNotNull(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        registerValueWithKey(parameterBinder, null, value);
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" is not null");
        return this;
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
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" > ").append(name2);
        return this;
    }

    @Override
    public Condition andGreaterThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" > ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
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
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" > ").append(name2);
        return this;
    }

    @Override
    public Condition orGreaterThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" > ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T, F> Condition andGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(name)
                .append(" >= ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" >= ").append(name2);
        return this;
    }

    @Override
    public Condition andGreaterThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" >= ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T, F> Condition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" >= ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" >= ").append(name2);
        return this;
    }

    @Override
    public Condition orGreaterThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" >= ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
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
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public Condition andLessThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" < ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T, F> Condition orLessThan(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" < ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public Condition orLessThan(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" < ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T, F> Condition andLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(name)
                .append(" <= ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public Condition andLessThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        condition.append(" ").append(logicalOperatorType(AND)).append(functionToString)
                .append(" <= ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public <T, F> Condition orLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        String name = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(name)
                .append(" <= ").append(registerValueWithKey(parameterBinder, fn, value));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        String name = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String name2 = SqlUtils.extractQualifiedAlias(field2, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR))
                .append(name).append(" <= ").append(name2);
        return this;
    }

    @Override
    public Condition orLessThanOrEqualTo(ColumFunction columFunction, Object value) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR)).append(functionToString)
                .append(" <= ").append(registerValueWithKey(parameterBinder, null, value));
        return this;
    }

    @Override
    public <T, F> Condition andIn(Fn<T, F> fn, Iterable<?> values) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public Condition andIn(Column column, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(AND));
        String functionToString = column.getFunctionToString(sqlDialect(), version);
        condition.append(functionToString).append(" in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public Condition andIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(functionToString).append(" in");
        iteratingCollection(values);
        return this;
    }


    @Override
    public <T, F> Condition orIn(Fn<T, F> fn, Iterable<?> values) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public Condition orIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(functionToString).append(" in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public <T, F> Condition andNotIn(Fn<T, F> fn, Iterable<?> values) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public Condition andNotIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(functionToString).append(" not in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public <T, F> Condition orNotIn(Fn<T, F> fn, Iterable<?> values) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not in");
        iteratingFnCollection(fn, values.iterator());
        return this;
    }

    @Override
    public Condition orNotIn(ColumFunction columFunction, Iterable<?> values) {
        String functionToString = columFunction.getFunctionToString(sqlDialect(), version);
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(functionToString).append(" not in");
        iteratingCollection(values);
        return this;
    }

    @Override
    public <T, F> Condition andBetween(Fn<T, F> fn, Object start, Object end) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" between ").append(registerValueWithKey(parameterBinder, fn, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end));
        return this;
    }

    @Override
    public <T1, T2, F> Condition andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        String column = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String column2 = SqlUtils.extractQualifiedAlias(startField, aliasTableMap, dataSourceName);
        String column3 = SqlUtils.extractQualifiedAlias(endField, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" between ").append(column2)
                .append(" and ").append(column3);
        return this;
    }

    @Override
    public <T, F> Condition orBetween(Fn<T, F> fn, Object start, Object end) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" between ").append(registerValueWithKey(parameterBinder, fn, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end));
        return this;
    }

    @Override
    public <T1, T2, F> Condition orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        String column = SqlUtils.extractQualifiedAlias(field1, aliasTableMap, dataSourceName);
        String column2 = SqlUtils.extractQualifiedAlias(startField, aliasTableMap, dataSourceName);
        String column3 = SqlUtils.extractQualifiedAlias(endField, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" between ").append(column2)
                .append(" and ").append(column3);
        return this;
    }

    @Override
    public <T, F> Condition andNotBetween(Fn<T, F> fn, Object start, Object end) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not between ").append(registerValueWithKey(parameterBinder, fn, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end));
        return this;
    }

    @Override
    public <T, F> Condition orNotBetween(Fn<T, F> fn, Object start, Object end) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not between ").append(registerValueWithKey(parameterBinder, fn, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, fn, end));
        return this;
    }

    @Override
    public <T, F> Condition andLike(Fn<T, F> fn, String pattern) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" like ").append(registerValueWithKey(parameterBinder, fn, pattern));
        return this;
    }

    @Override
    public <T, F> Condition orLike(Fn<T, F> fn, String pattern) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" like ").append(registerValueWithKey(parameterBinder, fn, pattern));
        return this;
    }

    @Override
    public <T, F> Condition andNotLike(Fn<T, F> fn, String pattern) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND)).append(column)
                .append(" not like ").append(registerValueWithKey(parameterBinder, fn, pattern));
        return this;
    }

    @Override
    public <T, F> Condition orNotLike(Fn<T, F> fn, String pattern) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR)).append(column)
                .append(" not like ").append(registerValueWithKey(parameterBinder, fn, pattern));
        return this;
    }

    @Override
    public <T, F> Condition andMatches(Fn<T, F> fn, String regex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> Condition orMatches(Fn<T, F> fn, String regex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Condition limit(int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Condition limit(int limit) {
        throw new UnsupportedOperationException();
    }

    public String getWhereConditionSyntax() {
        return condition.toString();
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    protected String logicalOperatorType(LogicalOperatorType logicalOperatorType) {
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
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }


    @Override
    public HavingCondition andEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition orEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition orEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" = ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition andNotEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition andNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition orNotEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition orNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" != ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition andGreaterThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition andGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition orGreaterThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition orGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" > ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition andGreaterThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition andGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition orGreaterThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition orGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" >= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition andLessThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition andLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition orLessThan(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition orLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" < ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition andLessThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition andLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition orLessThanOrEqualTo(AggregateFunction function, Object value) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(registerValueWithKey(parameterBinder, value));
        return this;
    }

    @Override
    public HavingCondition orLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" ").append(executeFunctionToString(function)).append(" <= ")
                .append(nestedSelectSql(nestedSelect));
        return this;
    }

    @Override
    public HavingCondition andIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public HavingCondition andIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public HavingCondition orIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public HavingCondition orIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public HavingCondition andNotIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" not in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public HavingCondition andNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public HavingCondition orNotIn(AggregateFunction function, Iterable<?> values) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" not in");
        iteratingFnCollection(null, values.iterator());
        return this;
    }

    @Override
    public HavingCondition orNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" not in (").append(nestedSelectSql(nestedSelect)).append(")");
        return this;
    }

    @Override
    public HavingCondition andBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public HavingCondition orBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public HavingCondition andNotBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(AND))
                .append(executeFunctionToString(function))
                .append(" not between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
    }

    @Override
    public HavingCondition orNotBetween(AggregateFunction function, Object start, Object end) {
        condition.append(" ").append(logicalOperatorType(OR))
                .append(executeFunctionToString(function))
                .append(" not between ").append(registerValueWithKey(parameterBinder, start))
                .append(" and ").append(registerValueWithKey(parameterBinder, end));
        return this;
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
            condition.append(registerValueWithKey(parameterBinder, fn, value));
            if (iterator.hasNext()) {
                condition.append(", ");
            }
        }
        condition.append(")");
    }

    protected SqlDialect sqlDialect() {
        throw new UnsupportedOperationException("Unimplemented SQL dialects");
    }
}
