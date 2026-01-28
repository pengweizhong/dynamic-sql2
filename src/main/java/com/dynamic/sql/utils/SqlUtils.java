/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.utils;

import com.dynamic.sql.context.SchemaContextHolder;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.GroupFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.AbstractAliasHelper;
import com.dynamic.sql.core.column.AbstractAliasHelper.TableAliasImpl;
import com.dynamic.sql.core.column.function.AnonymousFunction;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.condition.impl.dialect.MysqlWhereCondition;
import com.dynamic.sql.core.condition.impl.dialect.OracleWhereCondition;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.dml.SqlStatementWrapper.BatchType;
import com.dynamic.sql.core.dml.select.NestedMeta;
import com.dynamic.sql.core.dml.select.Select;
import com.dynamic.sql.core.dml.select.build.*;
import com.dynamic.sql.core.dml.select.build.join.FromNestedJoin;
import com.dynamic.sql.core.dml.select.build.join.JoinTable;
import com.dynamic.sql.core.dml.select.build.join.NestedJoin;
import com.dynamic.sql.core.dml.select.order.CustomOrderBy;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceMeta;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.enums.*;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.FieldMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.table.view.ViewColumnMeta;
import com.dynamic.sql.table.view.ViewMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
//            throw new IllegalArgumentException("Identifier cannot be empty");
            return "";
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

    /**
     * 按照当前SQL语义匹配最佳表别名
     *
     * @param originalClassCanonicalName 查询表类全称
     * @param aliasTableMap              当前会话匹配到的别名
     * @param tableMeta                  原始表meta
     */
    public static String extractQualifiedAlias(String originalClassCanonicalName, Map<String, TableAliasMapping> aliasTableMap, TableMeta tableMeta) {
        if (aliasTableMap == null && tableMeta != null) {
            return tableMeta.getTableAlias();
        }
        TableAliasMapping alias = aliasTableMap.get(originalClassCanonicalName);
        if (alias != null) {
            String alias1 = alias.getAlias();
            if (alias1 == null && tableMeta != null) {
                return tableMeta.getTableAlias();
            }
            return alias1;
        }
        return tableMeta != null ? ifAbsentAlias(tableMeta.getTableAlias(), null, aliasTableMap) : null;
    }

    public static <T, F> String extractQualifiedAlias(Fn<T, F> field, Map<String, TableAliasMapping> aliasTableMap, String dataSourceName) {
        return extractQualifiedAlias(null, field, aliasTableMap, dataSourceName, null);
    }

    public static <T, F> String extractQualifiedAlias(Fn<T, F> field, Map<String, TableAliasMapping> aliasTableMap, String dataSourceName, SqlExecuteType sqlExecuteType) {
        return extractQualifiedAlias(null, field, aliasTableMap, dataSourceName, sqlExecuteType);
    }

    /**
     * 按照当前SQL语义匹配最佳表别名, 之后拼接列名
     * TODO 应当将别名和列名分开处理
     */
    public static <T, F> String extractQualifiedAlias(String tableAlias, Fn<T, F> field, Map<String, TableAliasMapping> aliasTableMap, String dataSourceName, SqlExecuteType sqlExecuteType) {
        Fn fn = field;
        aliasTableMap = aliasTableMap == null ? new HashMap<>() : aliasTableMap;
        //如果内嵌了表别名，则此处的表别名优先级最高
        if (field instanceof AbstractAliasHelper) {
            AbstractAliasHelper abstractAlias = (AbstractAliasHelper) field;
            if (abstractAlias instanceof TableAliasImpl) {
                tableAlias = ifAbsentAlias(tableAlias, abstractAlias.getTableAlias(), aliasTableMap);
                if (abstractAlias.getFnColumn() != null) {
                    fn = abstractAlias.getFnColumn();
                } else {
                    DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
                    DbType dbType = dataSourceMeta.getDbType();
                    SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
                    tableAlias = SqlUtils.quoteIdentifier(sqlDialect, tableAlias);
                    if (isNeedAlias(sqlExecuteType)) {
                        return tableAlias + "." + SqlUtils.quoteIdentifier(sqlDialect, abstractAlias.getColumnName());
                    }
                    return SqlUtils.quoteIdentifier(sqlDialect, abstractAlias.getColumnName());
                }
            }
        }
        if (field instanceof GroupFn) {
            GroupFn groupFn = (GroupFn) field;
            tableAlias = ifAbsentAlias(tableAlias, groupFn.getTableAlias(), aliasTableMap);
            if (groupFn.getFn() != null) {
                fn = groupFn.getFn();
            } else {
                DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
                DbType dbType = dataSourceMeta.getDbType();
                SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
                if (isNeedAlias(sqlExecuteType)) {
                    tableAlias = SqlUtils.quoteIdentifier(sqlDialect, tableAlias);
                    return tableAlias + "." + SqlUtils.quoteIdentifier(sqlDialect, groupFn.getColumnName());
                }
                return SqlUtils.quoteIdentifier(sqlDialect, groupFn.getColumnName());
            }
        }
        //如果使用了当前回话的表别名
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(fn);
        if (isNeedAlias(sqlExecuteType) && tableAlias == null && !aliasTableMap.isEmpty()) {
            TableAliasMapping aliasMapping = aliasTableMap.get(originalClassCanonicalName);
            if (aliasMapping != null) {
                tableAlias = aliasMapping.getAlias();
            }
        }
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        if (tableMeta == null) {
            throw new DynamicSqlException("Table mapping failed to load: " + originalClassCanonicalName);
        }
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
        DbType dbType = dataSourceMeta.getDbType();
        SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
        ColumnMeta columnMeta = tableMeta.getColumnMeta(ReflectUtils.fnToFieldName(fn));
        //最后匹配全局的表别名，通常默认别名就是表名
        tableAlias = ifAbsentAlias(tableAlias, tableMeta.getTableAlias(), aliasTableMap);
        TableAliasMapping aliasMapping = aliasTableMap.get(tableAlias);
        String column = matchBestColumnName(aliasMapping, columnMeta, sqlDialect);
        column = SqlUtils.quoteIdentifier(sqlDialect, column);
//        String column = SqlUtils.quoteIdentifier(sqlDialect, aliasMapping != null && aliasMapping.isNestedJoin() ? columnMeta.getField().getName() : columnMeta.getColumnName());
        if (isNeedAlias(sqlExecuteType)) {
            return SqlUtils.quoteIdentifier(sqlDialect, tableAlias) + "." + column;
        }
        return column;
    }

    private static String matchBestColumnName(TableAliasMapping aliasMapping, ColumnMeta columnMeta, SqlDialect sqlDialect) {
        return aliasMapping != null && aliasMapping.isNestedJoin() ? columnMeta.getField().getName() : columnMeta.getColumnName();
    }

    /**
     * 别名是否存在
     *
     * @param oriAlias
     * @param newAlias
     * @param aliasTableMap
     * @return
     */
    private static String ifAbsentAlias(String oriAlias, String newAlias, Map<String, TableAliasMapping> aliasTableMap) {
        if (oriAlias == null && newAlias != null) {
            return newAlias;
        }
        //判断是否强制使用表别名
        if (aliasTableMap.containsKey("***")) {
            return aliasTableMap.get("***").getAlias();
        }
        //如果有通用别名
        if (oriAlias == null) {
            if (aliasTableMap.get("**") != null) {
                return aliasTableMap.get("**").getAlias();
            }
        }
        return oriAlias;
    }

    /**
     * 是否需要拼接别名
     *
     * @param sqlExecuteType sql执行类型
     * @return true 需要拼接别名，false 不需要拼接别名
     */
    public static boolean isNeedAlias(SqlExecuteType sqlExecuteType) {
        if (sqlExecuteType == null) {
            return true;
        }
        //为空 或者  只是查询时，才需要表别名
        return Objects.equals(sqlExecuteType.getType(), DMLType.SELECT.getType());
    }

    public static String extractQualifiedAliasOrderBy(OrderBy orderBy,
                                                      Map<String, TableAliasMapping> aliasTableMap,
                                                      String dataSourceName,
                                                      Version version,
                                                      ParameterBinder parameterBinder,
                                                      Boolean isFromNestedSelect,
                                                      Class<?> returnClass) {
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        DbType dbType = dataSourceMeta.getDbType();
        SqlDialect sqlDialect = SqlDialect.valueOf(dbType.name());
        StringBuilder sqlBuilder = new StringBuilder(" ");
        if (orderBy instanceof CustomOrderBy) {
            CustomOrderBy customOrderBy = (CustomOrderBy) orderBy;
            //自定义排序直接append SQL注入交给调用者自行控制
            sqlBuilder.append(customOrderBy.getOrderingFragment());
        }
        if (orderBy instanceof DefaultOrderBy) {
            DefaultOrderBy defaultOrderBy = (DefaultOrderBy) orderBy;
            //通过类排序的方式不存在SQL注入的风险
            if (defaultOrderBy.getFieldFn() != null) {
                String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(defaultOrderBy.getFieldFn());
                TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
                FieldMeta fieldMeta;
                if (tableMeta != null) {
                    fieldMeta = tableMeta.getColumnMetaByFieldName(ReflectUtils.fnToFieldName(defaultOrderBy.getFieldFn()));
                } else {
                    Class<?> aClass = ReflectUtils.loadClass(originalClassCanonicalName);
                    ViewMeta viewMeta = TableProvider.getViewMeta(aClass);
                    fieldMeta = viewMeta.getViewColumnMetaByFieldName(ReflectUtils.fnToFieldName(defaultOrderBy.getFieldFn()));
                }
//                ColumnMeta columnMeta = tableMeta.getColumnMeta(ReflectUtils.fnToFieldName(defaultOrderBy.getFieldFn()));
                String tableAlias = extractQualifiedAlias(originalClassCanonicalName, aliasTableMap, tableMeta);
                //进行排序（ORDER BY）时，是否使用 原始列名 还是 别名，取决于 SQL 的结构和查询的阶段。
                //子查询不需要限定表别名，因为字段别名已经是唯一的了
                //除非用户强制指定别名
                if (Objects.equals(isFromNestedSelect, Boolean.TRUE)) {
                    if (defaultOrderBy.getTableAlias() != null) {
                        sqlBuilder.append(quoteIdentifier(sqlDialect, defaultOrderBy.getTableAlias())).append(".");
                    }
                    sqlBuilder.append(quoteIdentifier(sqlDialect, fieldMeta.getField().getName()));
                    return sqlBuilder.toString();
                }
                if (defaultOrderBy.getTableAlias() != null) {
                    sqlBuilder.append(quoteIdentifier(sqlDialect, defaultOrderBy.getTableAlias())).append(".");
                } else if (defaultOrderBy.getTableAlias() == null && aliasTableMap != null && tableAlias != null) {
                    sqlBuilder.append(quoteIdentifier(sqlDialect, tableAlias)).append(".");
                }
//                else {
//                    sqlBuilder.append(quoteIdentifier(sqlDialect, tableMeta.getTableAlias())).append(".");
//                }
                sqlBuilder.append(quoteIdentifier(sqlDialect, fieldMeta.getColumnName()));
            } else if (defaultOrderBy.getColumFunction() != null) {
                ColumFunction columFunction = defaultOrderBy.getColumFunction();
                String functionToString = columFunction.getFunctionToString(sqlDialect, version, aliasTableMap);
                parameterBinder.addParameterBinder(columFunction.getParameterBinder());
                sqlBuilder.append(functionToString);
            } else {
                if (StringUtils.isNotBlank(orderBy.getTableAlias())) {
                    sqlBuilder.append(orderBy.getTableAlias()).append(".");
                }
                //校验SQL风险
                checkSqlInjection(dataSourceName, returnClass, defaultOrderBy.getColumnName());
                sqlBuilder.append(quoteIdentifier(sqlDialect, defaultOrderBy.getColumnName()));
            }
        }
        return sqlBuilder.toString();
    }

    /**
     * 校验SQL注入风险
     */
    private static void checkSqlInjection(String dataSourceName, Class<?> returnClass, String columnName) {
        //没有就不校验
        if (returnClass == null) {
            return;
        }
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        if (!schemaProperties.isCheckSqlInjection()) {
            return;
        }
        //校验列名是否存在SQL注入风险
        ViewMeta fieldMeta = TableProvider.getViewMeta(returnClass);
        ViewColumnMeta viewColumnMeta = fieldMeta.getViewColumnMetaByFieldName(columnName);
        ViewColumnMeta columnMeta = viewColumnMeta == null
                ? fieldMeta.getViewColumnMetaByColumnName(columnName) : viewColumnMeta;
        if (columnMeta == null) {
            throw new DynamicSqlException("Order by column name '" + columnName
                    + "' not found in  '" + returnClass.getCanonicalName() + "'. Possible SQL injection risk.");
        }
    }

    public static Object formattedParameter(/*SqlDialect sqlDialect,*/ Object value) {
//        if (value instanceof String) {
//            return "'" + value + "'";
//        }
//        if (value instanceof AnonymousFunction) {
//            AnonymousFunction anonymousFunction = (AnonymousFunction) value;
//            return anonymousFunction.getFunctionToString();
//        }
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

    public static String getSyntaxUnion(SqlDialect sqlDialect, UnionType unionType) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return unionType == UnionType.UNION ? "union" : "union all";
            default:
                return unionType == UnionType.UNION ? "UNION" : "UNION ALL";
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
                                                                            Map<String, TableAliasMapping> aliasTableMap, String dataSourceName) {
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
        if (tableMeta == null) {
            throw new DynamicSqlException("不存在表元数据，请检查类是否正确注册。例如类名未使用@Table注解标记，或者未在配置文件中注册。错误发生的表类名：" + fromTableClass.getName());
        }
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        return schemaProperties.getSqlDialect();
    }

    public static <C extends WhereCondition<C>> SqlSelectBuilder matchSqlSelectBuilder(SelectSpecification selectSpecification,
                                                                                       Map<String, TableAliasMapping> aliasTableMap) {
        NestedMeta nestedMeta = selectSpecification.getNestedMeta();
        SqlDialect sqlDialect = nestedMeta == null ? null : nestedMeta.getSqlDialect();
        if (sqlDialect == null) {
            JoinTable joinTable = selectSpecification.getJoinTables().get(0);
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
        }
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return new MysqlSqlSelectBuilder(selectSpecification, aliasTableMap);
            case ORACLE:
                return new OracleSqlSelectBuilder(selectSpecification, aliasTableMap);
            default:
                return new GenericSqlSelectBuilder(selectSpecification, aliasTableMap);
        }
    }

    public static SqlStatementSelectWrapper executeNestedSelect
            (Consumer<AbstractColumnReference> nestedSelectConsumer) {
        return executeNestedSelect(null, nestedSelectConsumer);
    }

    public static SqlStatementSelectWrapper executeNestedSelect(NestedMeta nestedMeta,
                                                                Consumer<AbstractColumnReference> nestedSelectConsumer) {
        return executeNestedSelect(nestedMeta, nestedSelectConsumer, new HashMap<>());
    }

    public static SqlStatementSelectWrapper executeNestedSelect(NestedMeta nestedMeta,
                                                                Consumer<AbstractColumnReference> nestedSelectConsumer,
                                                                Map<String, TableAliasMapping> aliasTableMap) {
        Select select = new Select(nestedMeta);
        AbstractColumnReference columnReference = select.loadColumReference();
        nestedSelectConsumer.accept(columnReference);
        SqlSelectBuilder nestedSqlBuilder = matchSqlSelectBuilder(select.getSelectSpecification(), aliasTableMap);
        return nestedSqlBuilder.build(null);
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
                List<Object> param = batchParameterBinder.getValues().stream()
                        .map(SqlUtils::formattedParameter).collect(Collectors.toList());
                preparedSql.addBatchParams(param);
            }
            return preparedSql;
        }
        //是追加模式
        List<ParameterBinder> batchParameterBinders = sqlStatementWrapper.getBatchParameterBinders();
        ArrayList<Object> params = new ArrayList<>();
        for (ParameterBinder batchParameterBinder : batchParameterBinders) {
            List<Object> param = batchParameterBinder.getValues().stream()
                    .map(SqlUtils::formattedParameter).collect(Collectors.toList());
            params.addAll(param);
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
                int start = matcher.start();
                int end = matcher.end();
                // 替换占位符为对应的值
                Object formattedParameter = SqlUtils.formattedParameter(parameterBinder.getValue(placeholder));
                if (formattedParameter instanceof AnonymousFunction) {
                    AnonymousFunction anonymousFunction = (AnonymousFunction) formattedParameter;
                    PreparedSql functionPreparedSql = parsePreparedObject(new StringBuilder(anonymousFunction.getFunctionToString()),
                            anonymousFunction.getParameterBinder());
                    params.addAll(functionPreparedSql.getParams());
                    modifiedSql.replace(start, end, functionPreparedSql.getSql());
                } else {
                    params.add(formattedParameter);
                    modifiedSql.replace(start, end, "?");
                }
                // 在修改 SQL 后，重新设置 matcher
                matcher = uuidPattern.matcher(modifiedSql);
            }
        }
        return new PreparedSql(modifiedSql.toString(), params);
    }

    public static String registerValueWithKey(ParameterBinder parameters, Object value) {
        return registerValueWithKey(parameters, null, value);
    }

    @SuppressWarnings("all")
    public static String registerValueWithKey(ParameterBinder parameters, Fn<?, ?> fn, Object value) {
        String key = generateBindingKey();
        //允许参数为null
        if (value == null) {
            parameters.add(key, value);
            return key;
        }
        //JDBC 规范没有直接支持 YearMonth 定义标准映射。因此这里直接转为字符串
        if (value.getClass() == YearMonth.class) {
            parameters.add(key, value.toString());
            return key;
        }
        //不需要任何特殊处理
        if (fn == null) {
            parameters.add(key, value);
            return key;
        }
        //纯字符串入参
        if (fn instanceof AbstractAliasHelper) {
            parameters.add(key, value);
            return key;
        }
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(fn);
        String fieldName = ReflectUtils.fnToFieldName(fn);
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        ColumnMeta columnMeta = tableMeta.getColumnMeta(fieldName);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        parameters.add(key, ConverterUtils.convertToDatabaseColumn(schemaProperties.getSqlDialect(), columnMeta, value));
        return key;
    }

    public static String generateBindingKey() {
        //Key 的构成："^:[0-9a-f]{32}$"
        return ":" + UUID.randomUUID().toString().replace("-", "");
    }

//    public static String replacePlaceholdersWithValues(SqlStatementWrapper sqlStatementWrapper) {
//        StringBuilder modifiedSql = new StringBuilder(sqlStatementWrapper.getRawSql());
//        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
//        Pattern uuidPattern = Pattern.compile(":[0-9a-f]{32}");
//        Matcher matcher = uuidPattern.matcher(sqlStatementWrapper.getRawSql());
//        while (matcher.find()) {
//            String placeholder = matcher.group();
//            if (parameterBinder.contains(placeholder)) {
//                Object formattedParameter = SqlUtils.formattedParameter(parameterBinder.getValue(placeholder));
//                // 替换占位符为对应的值
//                int start = matcher.start();
//                int end = matcher.end();
//                modifiedSql.replace(start, end, formattedParameter.toString());
//                matcher = uuidPattern.matcher(modifiedSql);
//            }
//        }
//        return modifiedSql.toString();
//    }

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

    public static Version databaseProductVersion(DbType dbType, String databaseProductVersion) {
        //Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production\nVersion 19.19.0.0.0
        //MySQL 5.6.16-log
        if (dbType == DbType.ORACLE) {
            int i = databaseProductVersion.indexOf("Version");
            if (i != -1) {
                databaseProductVersion = databaseProductVersion.substring(i + "Version".length()).trim();
            }
        }
        String[] split = databaseProductVersion.split("\\.");
        Integer majorVersionNumber = 0;
        if (split.length >= 1) {
            majorVersionNumber = Integer.parseInt(split[0]);
        }
        Integer minorVersionNumber = 0;
        if (split.length >= 2) {
            minorVersionNumber = Integer.parseInt(split[1]);
        }
        Integer patchVersionNumber = 0;
        if (split.length >= 3) {
            String string = split[2];
            if (string.contains("-")) {
                patchVersionNumber = Integer.parseInt(string.substring(0, string.indexOf("-")));
            } else {
                patchVersionNumber = Integer.parseInt(string);
            }
        }
        return new Version(majorVersionNumber, minorVersionNumber, patchVersionNumber);
    }

}
