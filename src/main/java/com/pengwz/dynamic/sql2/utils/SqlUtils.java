package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.GenericWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.MysqlWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.OracleWhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.build.*;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.*;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.enums.LogicalOperatorType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.Map;
import java.util.function.Consumer;

public class SqlUtils {
    private SqlUtils() {
    }

    /**
     * 根据 SQL 方言返回用于表名和列名的合适包裹符号。
     * 不同的 SQL 方言使用不同的符号来包裹标识符（例如，表名或列名）。
     *
     * @param sqlDialect SQL 方言（例如，MYSQL、ORACLE、POSTGRESQL 等）
     * @return 指定 SQL 方言所使用的包裹符号字符串
     */
    public static String getSqlTypeQuotes(SqlDialect sqlDialect) {
        if (sqlDialect == null) {
            throw new IllegalArgumentException("Get Sql type quotes not accepting null values");
        }
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "`";
            case ORACLE:
            case POSTGRESQL:
                return "\"";
            case SQLSERVER:
                return "[]";
            default:
                throw new IllegalArgumentException("Get Sql type quotes not accepting " + sqlDialect);
        }
    }

    /**
     * 根据 SQL 方言和给定的标识符返回包裹好的标识符。
     *
     * @param sqlDialect SQL 方言（例如，MYSQL、ORACLE、POSTGRESQL 等）
     * @param identifier 要包裹的标识符（例如，表名或列名）
     * @return 包裹好的标识符字符串
     */
    public static String quoteIdentifier(SqlDialect sqlDialect, String identifier) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier cannot be empty");
        }
        if (sqlDialect == null) {
            throw new IllegalArgumentException("SqlDialect cannot be null");
        }
        String quotes = getSqlTypeQuotes(sqlDialect);
        if (sqlDialect == SqlDialect.SQLSERVER) {
            String[] split = quotes.split("");
            return split[0] + identifier + split[1];
        }
        return quotes + identifier + quotes;
    }

    public static <T, F> String qualifiedAliasName(Fn<T, F> field) {
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(field);
        return qualifiedAliasName(originalClassCanonicalName, field, null);
    }

    public static <T, F> String qualifiedAliasName(Fn<T, F> field, Map<String, String> aliasTableMap) {
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(field);
        return qualifiedAliasName(originalClassCanonicalName, field, aliasTableMap.get(originalClassCanonicalName));
    }

    public static <T, F> String qualifiedAliasName(String originalClassCanonicalName, Fn<T, F> field, String alias) {
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
        DbType dbType = dataSourceMeta.getDbType();
        SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
        String tableAlias = SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableAlias());
        ColumnMeta columnMeta = tableMeta.getColumnMeta(ReflectUtils.fnToFieldName(field));
        String column = SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getColumnName());
        if (StringUtils.isEmpty(alias)) {
            return tableAlias + "." + column;
        }
        return SqlUtils.quoteIdentifier(sqlDialect, alias) + "." + column;
    }

    public static String getSyntaxSelect(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "select";
            default:
                return "SELECT";
        }
    }

    public static String getSyntaxAs(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "as";
            default:
                return "AS";
        }
    }

    public static String getSyntaxFrom(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "from";
            default:
                return "FROM";
        }
    }

    public static String getSyntaxJoin(SqlDialect sqlDialect, JoinTable joinTable) {
        if (joinTable instanceof InnerJoin) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "inner join";
                default:
                    return "INNER JOIN";
            }
        }
        if (joinTable instanceof LeftJoin) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "left join";
                default:
                    return "LEFT JOIN";
            }
        }
        if (joinTable instanceof RightJoin) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "right join";
                default:
                    return "RIGHT JOIN";
            }
        }
        if (joinTable instanceof FullJoin) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    throw new UnsupportedOperationException("Unsupported associated table query full join type.");
                default:
                    return "FULL OUTER JOIN";
            }
        }
        throw new UnsupportedOperationException("Unsupported associated table query type.");
    }

    public static String getSyntaxOn(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "on";
            default:
                return "ON";
        }
    }

    public static String getSyntaxWhere(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "where";
            default:
                return "WHERE";
        }
    }

    public static String getSyntaxLimit(SqlDialect sqlDialect) {
        return "limit";
    }

    public static String getSyntaxExists(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "exists";
            default:
                return "EXISTS";
        }
    }

    public static String getSyntaxGroupBy(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "group by";
            default:
                return "GROUP BY";
        }
    }

    public static String getSyntaxHaving(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "having";
            default:
                return "HAVING";
        }
    }

    public static String getSyntaxOrderBy(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "order by";
            default:
                return "ORDER BY";
        }
    }

    public static String getSyntaxLogicalOperator(LogicalOperatorType logicalOperatorType, SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return logicalOperatorType.name().toLowerCase();
            default:
                return logicalOperatorType.name();
        }
    }

    public static WhereCondition matchDialectCondition(SqlDialect sqlDialect, Version version, Map<String, String> aliasTableMap) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return new MysqlWhereCondition(version, aliasTableMap);
            case ORACLE:
                return new OracleWhereCondition(version, aliasTableMap);
            default:
                return new GenericWhereCondition(version, aliasTableMap);
        }
    }

    public static SqlDialect getSqlDialect(Class<?> fromTableClass) {
        TableMeta tableMeta = TableProvider.getTableMeta(fromTableClass);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        return schemaProperties.getSqlDialect();
    }

    public static SqlSelectBuilder matchSqlSelectBuilder(SelectSpecification selectSpecification) {
        FromJoin fromJoin = (FromJoin) selectSpecification.getJoinTables().get(0);
        SqlDialect sqlDialect = SqlUtils.getSqlDialect(fromJoin.getTableClass());
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return new MysqlSqlSelectBuilder(selectSpecification);
            case ORACLE:
                return new OracleSqlSelectBuilder(selectSpecification);
            default:
                return new GenericSqlSelectBuilder(selectSpecification);
        }
    }

    public static SqlSelectParam executeNestedSelect(Consumer<NestedSelect> nestedSelectConsumer) {
        NestedSelect nestedSelect = new NestedSelect();
        nestedSelectConsumer.accept(nestedSelect);
        SqlSelectBuilder nestedSqlBuilder = matchSqlSelectBuilder(nestedSelect.getSelectSpecification());
        SqlSelectParam sqlSelectParam = nestedSqlBuilder.build();
        System.out.println("测试内嵌列输出结果 ---> " + sqlSelectParam.getRawSql());
        return sqlSelectParam;
    }


}
