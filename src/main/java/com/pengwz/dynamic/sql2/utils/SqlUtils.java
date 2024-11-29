package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.GroupFn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper.TableAliasImpl;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.GenericWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.MysqlWhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.OracleWhereCondition;
import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper.BatchType;
import com.pengwz.dynamic.sql2.core.dml.select.Select;
import com.pengwz.dynamic.sql2.core.dml.select.build.*;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromNestedJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.NestedJoin;
import com.pengwz.dynamic.sql2.core.dml.select.order.CustomOrderBy;
import com.pengwz.dynamic.sql2.core.dml.select.order.DefaultOrderBy;
import com.pengwz.dynamic.sql2.core.dml.select.order.OrderBy;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.enums.JoinTableType;
import com.pengwz.dynamic.sql2.enums.LogicalOperatorType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtils {
    private SqlUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(SqlUtils.class);

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

    public static String extractQualifiedAliasOrderBy(OrderBy orderBy, Map<String, String> aliasTableMap, String dataSourceName) {
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        DbType dbType = dataSourceMeta.getDbType();
        SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
        StringBuilder sqlBuilder = new StringBuilder(" ");
        if (orderBy instanceof CustomOrderBy) {
            CustomOrderBy customOrderBy = (CustomOrderBy) orderBy;
            //自定义排序直接append
            sqlBuilder.append(customOrderBy.getOrderingFragment());
        }
        if (orderBy instanceof DefaultOrderBy) {
            DefaultOrderBy defaultOrderBy = (DefaultOrderBy) orderBy;
            if (defaultOrderBy.getTableAlias() != null) {
                sqlBuilder.append(quoteIdentifier(sqlDialect, defaultOrderBy.getTableAlias())).append(".");
            }
            if (defaultOrderBy.getFieldFn() != null) {
                String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(defaultOrderBy.getFieldFn());
                TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
                ColumnMeta columnMeta = tableMeta.getColumnMeta(ReflectUtils.fnToFieldName(defaultOrderBy.getFieldFn()));
                if (defaultOrderBy.getTableAlias() == null && aliasTableMap != null) {
                    sqlBuilder.append(quoteIdentifier(sqlDialect, tableMeta.getTableAlias())).append(".");
                }
                sqlBuilder.append(quoteIdentifier(sqlDialect, columnMeta.getColumnName()));
            } else {
                sqlBuilder.append(quoteIdentifier(sqlDialect, defaultOrderBy.getColumnName()));
            }
        }
        return sqlBuilder.toString();
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
            //在 Oracle 中，表别名不支持 AS 关键字，只能直接指定别名。
            case ORACLE:
                return "";
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
        if (joinTableType == JoinTableType.CROSS) {
            switch (sqlDialect) {
                case MYSQL:
                case MARIADB:
                    return "cross join";
                default:
                    return "CROSS JOIN";
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

    @SuppressWarnings("unchecked")
    public static <T extends GenericWhereCondition> T matchDialectCondition(SqlDialect sqlDialect, Version version,
                                                                            Map<String, String> aliasTableMap, String dataSourceName) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return (T) new MysqlWhereCondition(version, aliasTableMap, dataSourceName);
            case ORACLE:
                return (T) new OracleWhereCondition(version, aliasTableMap, dataSourceName);
            default:
                return (T) new GenericWhereCondition(version, aliasTableMap, dataSourceName);
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

    public static SqlStatementSelectWrapper executeNestedSelect(Consumer<AbstractColumnReference> nestedSelectConsumer) {
        Select select = new Select();
        AbstractColumnReference columnReference = select.loadColumReference();
        nestedSelectConsumer.accept(columnReference);
        SqlSelectBuilder nestedSqlBuilder = matchSqlSelectBuilder(select.getSelectSpecification());
        return nestedSqlBuilder.build();
    }

    private static SqlDialect nestedJoinSqlDialect(NestedJoin nestedJoin) {
        SqlStatementSelectWrapper sqlStatementWrapper = executeNestedSelect(nestedJoin.getNestedSelect());
        nestedJoin.setSqlStatementWrapper(sqlStatementWrapper);
        return SchemaContextHolder.getSchemaProperties(sqlStatementWrapper.getDataSourceName()).getSqlDialect();
    }

    public static PreparedSql parsePreparedObject(SqlStatementWrapper sqlStatementWrapper) {
        //判断是否是批量SQL，如果是批量的就按照顺序填充参数，而不需要替换
        BatchType batchType = sqlStatementWrapper.getBatchType();
        if (batchType == null) {
            StringBuilder rawSql = sqlStatementWrapper.getRawSql();
            ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
            return parsePreparedObject(rawSql, parameterBinder);
        }
        //如果是批量模式
        PreparedSql preparedSql = new PreparedSql(sqlStatementWrapper.getRawSql().toString());
        if (batchType == BatchType.BATCH) {
            List<ParameterBinder> batchParameterBinders = sqlStatementWrapper.getBatchParameterBinders();
            for (ParameterBinder batchParameterBinder : batchParameterBinders) {
                List<Object> param = new ArrayList<>(batchParameterBinder.getValues());
                preparedSql.addBatchParams(param);
            }
            return preparedSql;
        }
        //是追加模式
        List<ParameterBinder> batchParameterBinders = sqlStatementWrapper.getBatchParameterBinders();
        ArrayList<Object> params = new ArrayList<>();
        for (ParameterBinder batchParameterBinder : batchParameterBinders) {
            params.addAll(batchParameterBinder.getValues());
        }
        preparedSql.addBatchParams(params);
        return preparedSql;
    }

    public static PreparedSql parsePreparedObject(StringBuilder rawSql, ParameterBinder parameterBinder) {
        //判断是否是批量SQL，如果是批量的就按照顺序填充参数，而不需要替换
        StringBuilder modifiedSql = new StringBuilder(rawSql);
        Pattern uuidPattern = Pattern.compile(":[0-9a-f]{32}");
        Matcher matcher = uuidPattern.matcher(rawSql);
        ArrayList<Object> params = new ArrayList<>();
        while (matcher.find()) {
            String placeholder = matcher.group();
            if (parameterBinder.contains(placeholder)) {
                params.add(parameterBinder.getValue(placeholder));
                // 替换占位符为对应的值
                int start = matcher.start();
                int end = matcher.end();
                modifiedSql.replace(start, end, "?");
                matcher = uuidPattern.matcher(modifiedSql);
            }
        }
        return new PreparedSql(modifiedSql.toString(), params);
    }

    public static String registerValueWithKey(ParameterBinder parameters, Object value) {
        String key = generateBindingKey();
        parameters.add(key, value);
        return key;
    }

    public static String registerValueWithKey(ParameterBinder parameters, Fn<?, ?> fn, Object value) {
        String key = generateBindingKey();
        if (fn == null) {
            parameters.add(key, value);
            return key;
        }
        if (fn instanceof AbstractAliasHelper) {
            parameters.add(key, value);
            return key;
        }
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(fn);
        String fieldName = ReflectUtils.fnToFieldName(fn);
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        ColumnMeta columnMeta = tableMeta.getColumnMeta(fieldName);
        parameters.add(key, ConverterUtils.convertToDatabaseColumn(columnMeta, value));
        return key;
    }

    public static String generateBindingKey() {
        //Key 的构成："^:[0-9a-f]{32}$"
        return ":" + UUID.randomUUID().toString().replace("-", "");
    }

    public static String replacePlaceholdersWithValues(SqlStatementWrapper sqlStatementWrapper) {
        StringBuilder modifiedSql = new StringBuilder(sqlStatementWrapper.getRawSql());
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        Pattern uuidPattern = Pattern.compile(":[0-9a-f]{32}");
        Matcher matcher = uuidPattern.matcher(sqlStatementWrapper.getRawSql());
        while (matcher.find()) {
            String placeholder = matcher.group();
            if (parameterBinder.contains(placeholder)) {
                Object formattedParameter = SqlUtils.formattedParameter(parameterBinder.getValue(placeholder));
                // 替换占位符为对应的值
                int start = matcher.start();
                int end = matcher.end();
                modifiedSql.replace(start, end, formattedParameter.toString());
                matcher = uuidPattern.matcher(modifiedSql);
            }
        }
        return modifiedSql.toString();
    }

    public static void close(Connection connection, ResultSet resultSet, Statement statement) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Connection closed abnormally.", e);
            }
        }
        close(resultSet, statement);
    }

    public static void close(ResultSet resultSet, Statement statement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("ResultSet closed abnormally.", e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("Statement closed abnormally.", e);
            }
        }
    }

}
