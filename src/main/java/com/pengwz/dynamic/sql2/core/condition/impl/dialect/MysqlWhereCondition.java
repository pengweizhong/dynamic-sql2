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

public class MysqlWhereCondition extends GenericWhereCondition {

    public MysqlWhereCondition(Version version, Map<String, String> aliasTableMap, String dataSourceName) {
        super(version, aliasTableMap, dataSourceName);
    }

    @Override
    protected SqlDialect sqlDialect() {
        return SqlDialect.MYSQL;
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
        condition.append(" ").append(logicalOperatorType(logicalOperatorType));
        //使用 REGEXP_LIKE (MySQL 8.0+)
        if (version.isGreaterThanOrEqual(new Version(8, 0, 0))) {
            condition.append(" regexp_like(").append(column).append(", ")
                    .append(registerValueWithKey(parameterBinder, fn, regex)).append(")");
            return this;
        }
        //使用 REGEXP (MySQL 5.7+)
        condition.append(" ").append(column).append(" regexp ").append(registerValueWithKey(parameterBinder, fn, regex));
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
        condition.append(" ").append(logicalOperatorType(logicalOperatorType));
        //使用 REGEXP_LIKE (MySQL 8.0+)
        if (version.isGreaterThanOrEqual(new Version(8, 0, 0))) {
            condition.append(" regexp_like(").append(column).append(", ")
                    .append(registerValueWithKey(parameterBinder, fn, columFunction.getFunctionToString(sqlDialect(), version)))
                    .append(")");
            return this;
        }
        //使用 REGEXP (MySQL 5.7+)
        condition.append(" ").append(column).append(" regexp ").append(registerValueWithKey(parameterBinder, fn,
                columFunction.getFunctionToString(sqlDialect(), version)));

        return this;
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        condition.append(" ").append(logicalOperatorType(AND));
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        //REPLACE(str, from_str, to_str)
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append("replace(").append(column).append(", ").append(registerValueWithKey(parameterBinder, fn, separator))
                .append(", ',')").append(")");
        return this;
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        condition.append(" ").append(logicalOperatorType(OR));
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        //REPLACE(str, from_str, to_str)
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append("replace(").append(column).append(", ").append(registerValueWithKey(parameterBinder, fn, separator))
                .append(", ',')").append(")");
        return this;
    }

    @Override
    public <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn,
                        columFunction.getFunctionToString(sqlDialect(), version)))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn,
                        columFunction.getFunctionToString(sqlDialect(), version)))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }


}
