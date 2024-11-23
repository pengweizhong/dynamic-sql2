package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.database.impl.MysqlSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.impl.OracleSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.core.database.parser.MysqlParser;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.datasource.connection.ConnectionHolder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.sql.Connection;
import java.util.Collection;
import java.util.function.Function;

public class SqlExecutionFactory {
    private SqlExecutionFactory() {
    }

    public static AbstractDialectParser chosenDialectParser(SchemaProperties schemaProperties,
                                                            Class<?> entityClass,
                                                            Collection<Object> param) {
        return chosenDialectParser(schemaProperties, entityClass, param, null);
    }

    public static AbstractDialectParser chosenDialectParser(SchemaProperties schemaProperties,
                                                            Class<?> entityClass,
                                                            Collection<Object> param,
                                                            WhereCondition whereCondition) {
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return new MysqlParser(entityClass, schemaProperties, param, whereCondition);
            default:
                throw new UnsupportedOperationException("Unsupported dialect: " + sqlDialect);
        }
    }

    public static <R> R executorSql(DMLType dmlType,
                                    SqlStatementWrapper sqlStatementWrapper,
                                    Function<SqlExecutor, R> doSqlExecutor) {
        String dataSourceName = sqlStatementWrapper.getDataSourceName();
        //添加拦截器
        SqlInterceptorChain sqlInterceptorChain = SqlInterceptorChain.getInstance();
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        Connection connection = null;
        Exception exception = null;
        R apply = null;
        PreparedSql preparedSql = null;
        try {
            connection = ConnectionHolder.getConnection(dataSourceMeta.getDataSource());
            boolean beforeExecution = sqlInterceptorChain.beforeExecution(sqlStatementWrapper, connection);
            preparedSql = SqlUtils.parsePreparedObject(sqlStatementWrapper);
            if (beforeExecution) {
                apply = applySql(dmlType, dataSourceName, connection, preparedSql, true, doSqlExecutor);
            } else {
                apply = sqlInterceptorChain.retrieveSkippedResult(sqlStatementWrapper, connection);
            }
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                ConnectionHolder.releaseConnection(connection);
            } finally {
                sqlInterceptorChain.afterExecution(preparedSql, apply, exception);
            }
        }
        return apply;
    }

    @SuppressWarnings("all")
    public static <R> R applySql(DMLType dmlType,
                                 String dataSourceName,
                                 Connection connection,
                                 PreparedSql preparedSql,
                                 boolean isIntercepted,
                                 Function<SqlExecutor, R> doSqlExecutor) {
        SqlExecutor sqlExecutor;
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        switch (schemaProperties.getSqlDialect()) {
            case MYSQL:
                sqlExecutor = new MysqlSqlExecutor(connection, preparedSql);
                break;
            case ORACLE:
                sqlExecutor = new OracleSqlExecutor(connection, preparedSql);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported sql dialect" + schemaProperties.getSqlDialect());
        }
        SchemaProperties.PrintSqlProperties printSqlProperties = schemaProperties.getPrintSqlProperties();
        SqlDebugger.debug(printSqlProperties, preparedSql, dataSourceName, isIntercepted);
        R applyResult = doSqlExecutor.apply(sqlExecutor);
        SqlDebugger.debug(printSqlProperties, dmlType, dataSourceName, applyResult);
        return applyResult;
    }

}
