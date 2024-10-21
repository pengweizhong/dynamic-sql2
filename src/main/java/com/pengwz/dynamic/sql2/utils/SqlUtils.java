package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.GroupFn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper.TableAliasImpl;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.GenericWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.MysqlWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.OracleWhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.Select;
import com.pengwz.dynamic.sql2.core.dml.select.build.*;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromNestedJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.NestedJoin;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.enums.JoinTableType;
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
//            String[] split = quotes.split("");
//            return split[0] + identifier + split[1];
            return "[" + identifier + "]";
        }
        return quotes + identifier + quotes;
    }

    public static <T, F> String extractQualifiedAlias(Fn<T, F> field, Map<String, String> aliasTableMap, String dataSourceName) {
        String tableAlias = null;
        Fn fn = field;
        //如果内嵌了表别名，则此处的表别名优先级最高
        if (field instanceof AbstractAliasHelper) {
            AbstractAliasHelper abstractAlias = (AbstractAliasHelper) field;
            if (abstractAlias instanceof TableAliasImpl) {
                tableAlias = abstractAlias.getTableAlias();
                if (abstractAlias.getFnColumn() != null) {
                    fn = abstractAlias.getFnColumn();
                } else {
                    DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
                    DbType dbType = dataSourceMeta.getDbType();
                    SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
                    tableAlias = StringUtils.isBlank(tableAlias) ? "" : SqlUtils.quoteIdentifier(sqlDialect, tableAlias);
                    return tableAlias + "." + SqlUtils.quoteIdentifier(sqlDialect, abstractAlias.getColumnName());
                }
            }
//            //像这种直接写了原始列的，是没有办法判断想请求哪个数据源，因此只能依靠外部传入
//            //所有只有当对象是OriginColumnAliasImpl 的时候，dataSourceName才能保证不为空，其他状态下获取dataSourceName没有意义
//            if (abstractAlias instanceof OriginColumnAliasImpl) {
//                DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
//                DbType dbType = dataSourceMeta.getDbType();
//                SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
//                return abstractAlias.getAbsoluteColumn(sqlDialect);
//            }
        }
        if (field instanceof GroupFn) {
            GroupFn groupFn = (GroupFn) field;
            tableAlias = groupFn.getTableAlias();
            if (groupFn.getFn() != null) {
                fn = groupFn.getFn();
            } else {
                DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
                DbType dbType = dataSourceMeta.getDbType();
                SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
                tableAlias = StringUtils.isBlank(tableAlias) ? "" : SqlUtils.quoteIdentifier(sqlDialect, tableAlias);
                return tableAlias + "." + SqlUtils.quoteIdentifier(sqlDialect, groupFn.getColumnName());
            }
        }
        //如果使用了当前回话的表别名
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(fn);
        if (tableAlias == null && aliasTableMap != null) {
            tableAlias = aliasTableMap.get(originalClassCanonicalName);
        }
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
        DbType dbType = dataSourceMeta.getDbType();
        SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
        //最后匹配全局的表别名，通常默认别名就是表名
        if (tableAlias == null) {
            tableAlias = tableMeta.getTableAlias();
        }
        ColumnMeta columnMeta = tableMeta.getColumnMeta(ReflectUtils.fnToFieldName(fn));
        String column = SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getColumnName());
        return SqlUtils.quoteIdentifier(sqlDialect, tableAlias) + "." + column;
    }

    public static Object formattedParameter(/*SqlDialect sqlDialect,*/ Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value;
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

    public static String getSyntaxJoin(SqlDialect sqlDialect, JoinTableType joinTableType) {
        if (joinTableType == JoinTableType.INNER) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "inner join";
                default:
                    return "INNER JOIN";
            }
        }
        if (joinTableType == JoinTableType.LEFT) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "left join";
                default:
                    return "LEFT JOIN";
            }
        }
        if (joinTableType == JoinTableType.RIGHT) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "right join";
                default:
                    return "RIGHT JOIN";
            }
        }
        if (joinTableType == JoinTableType.FULL) {
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

    public static WhereCondition matchDialectCondition(SqlDialect sqlDialect, Version version,
                                                       Map<String, String> aliasTableMap, String dataSourceName) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return new MysqlWhereCondition(version, aliasTableMap, dataSourceName);
            case ORACLE:
                return new OracleWhereCondition(version, aliasTableMap, dataSourceName);
            default:
                return new GenericWhereCondition(version, aliasTableMap, dataSourceName);
        }
    }

    public static SqlDialect getSqlDialect(Class<?> fromTableClass) {
        TableMeta tableMeta = TableProvider.getTableMeta(fromTableClass);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        return schemaProperties.getSqlDialect();
    }

    public static SqlSelectBuilder matchSqlSelectBuilder(SelectSpecification selectSpecification) {
        JoinTable joinTable = selectSpecification.getJoinTables().get(0);
        SqlDialect sqlDialect = null;
        if (joinTable instanceof FromNestedJoin) {
            FromNestedJoin fromNestedJoin = (FromNestedJoin) joinTable;
            NestedJoin nestedJoin = fromNestedJoin.getNestedJoin();
            sqlDialect = nestedJoinSqlDialect(nestedJoin);
        }
        if (joinTable instanceof NestedJoin) {
            NestedJoin nestedJoin = (NestedJoin) joinTable;
            sqlDialect = nestedJoinSqlDialect(nestedJoin);
        }
        if (sqlDialect == null) {
            sqlDialect = SqlUtils.getSqlDialect(joinTable.getTableClass());
        }
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

    public static SqlStatementWrapper executeNestedSelect(Consumer<AbstractColumnReference> nestedSelectConsumer) {
        Select select = new Select();
        AbstractColumnReference columnReference = select.loadColumReference();
        nestedSelectConsumer.accept(columnReference);
        SqlSelectBuilder nestedSqlBuilder = matchSqlSelectBuilder(select.getSelectSpecification());
        return nestedSqlBuilder.build();
    }

    private static SqlDialect nestedJoinSqlDialect(NestedJoin nestedJoin) {
        SqlStatementWrapper sqlStatementWrapper = executeNestedSelect(nestedJoin.getNestedSelect());
        nestedJoin.setSqlStatementWrapper(sqlStatementWrapper);
        return SchemaContextHolder.getSchemaProperties(sqlStatementWrapper.getDataSourceName()).getSqlDialect();
    }

}
