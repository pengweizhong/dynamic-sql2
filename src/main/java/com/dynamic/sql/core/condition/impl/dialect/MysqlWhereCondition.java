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
    public <T, F> GenericWhereCondition orMatches(Fn<T, F> fn, String regex) {
        return matches(OR, fn, regex);
    }

    public <T, F> GenericWhereCondition matches(LogicalOperatorType logicalOperatorType, Fn<T, F> fn, String regex) {
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
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, Object item) {
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        condition.append(" ").append(logicalOperatorType(AND));
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        //REPLACE(str, from_str, to_str)
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append("replace(").append(column).append(", ").append(registerValueWithKey(parameterBinder, null, separator))
                .append(", ',')").append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, Object item) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        condition.append(" ").append(logicalOperatorType(OR));
        String column = SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName);
        //REPLACE(str, from_str, to_str)
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn, item))
                .append(", ").append("replace(").append(column).append(", ").append(registerValueWithKey(parameterBinder, null, separator))
                .append(", ',')").append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(AND));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn,
                        columFunction.getFunctionToString(sqlDialect(), version)))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public <T, F> GenericWhereCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        parameterBinder.addParameterBinder(columFunction.getParameterBinder());
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" find_in_set(").append(registerValueWithKey(parameterBinder, fn,
                        columFunction.getFunctionToString(sqlDialect(), version)))
                .append(", ").append(SqlUtils.extractQualifiedAlias(fn, aliasTableMap, dataSourceName)).append(")");
        return this;
    }

    @Override
    public GenericWhereCondition limit(int limit) {
        return limit(0, limit);
    }

    @Override
    public GenericWhereCondition limit(int offset, int limit) {
        condition.append(" ").append(logicalOperatorType(OR));
        condition.append(" limit ").append(registerValueWithKey(parameterBinder, null, offset))
                .append(", ").append(registerValueWithKey(parameterBinder, null, limit));
        return this;
    }

}
