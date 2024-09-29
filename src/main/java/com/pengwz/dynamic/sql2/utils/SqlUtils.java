package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.MysqlWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.OracleWhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.build.*;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

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

    public static String getSyntaxInnerJoin(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return "inner join";
            default:
                return "INNER JOIN";
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

    public static <T, F> String qualifiedAliasName(Fn<T, F> field) {
        return qualifiedAliasName(field, null);
    }

    public static <T, F> String qualifiedAliasName(Fn<T, F> field, String alias) {
        TableMeta tableMeta = TableProvider.getTableMeta(ReflectUtils.getOriginalClassCanonicalName(field));
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

    public static WhereCondition matchDialectCondition(SqlDialect sqlDialect, Version version, String dataSourceName) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return new MysqlWhereCondition(version, dataSourceName);
            case ORACLE:
                return new OracleWhereCondition(version, dataSourceName);
            default:
                throw new UnsupportedOperationException(sqlDialect.name() + " dialect does not yet support parsing conditions");
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
        System.out.println("测试内嵌列输出结果 ---> " + sqlSelectParam.getSql());
        return sqlSelectParam;
    }


}
