package com.pengwz.dynamic.sql2.core.condition.impl.dialect;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.FunctionCondition;
import com.pengwz.dynamic.sql2.enums.LogicalOperatorType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.Map;

import static com.pengwz.dynamic.sql2.enums.LogicalOperatorType.AND;
import static com.pengwz.dynamic.sql2.enums.LogicalOperatorType.OR;
import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class OracleWhereCondition extends GenericWhereCondition {

    public OracleWhereCondition(Version version, Map<String, String> aliasTableMap, String dataSourceName) {
        super(version, aliasTableMap, dataSourceName);
    }

    @Override
    protected SqlDialect sqlDialect() {
        return SqlDialect.ORACLE;
    }

    @Override
    public <T, F> Condition andMatches(Fn<T, F> fn, String regex) {
        return matches(AND, fn, regex);
    }

    @Override
    public <T, F> Condition orMatches(Fn<T, F> fn, String regex) {
        return matches(OR, fn, regex);
    }

    public <T, F> Condition matches(LogicalOperatorType logicalOperatorType, Fn<T, F> fn, String regex) {
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType);
        condition.append(" REGEXP_LIKE(").append(column).append(", ")
                .append(registerValueWithKey(parameterBinder, fn, regex)).append(")");
        return this;
    }

    @Override
    public <T, F> FunctionCondition andMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return matchesFunction(AND, fn, columFunction);
    }

    @Override
    public <T, F> FunctionCondition orMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return matchesFunction(OR, fn, columFunction);
    }

    public <T, F> FunctionCondition matchesFunction(LogicalOperatorType logicalOperatorType, Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType);
        condition.append(" REGEXP_LIKE(").append(column).append(", ")
                .append(registerValueWithKey(parameterBinder, fn, columFunction.getFunctionToString(sqlDialect(), version)))
                .append(")");
        return this;
    }
}
