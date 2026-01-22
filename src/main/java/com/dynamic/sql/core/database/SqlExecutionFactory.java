/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database;


import com.dynamic.sql.context.SchemaContextHolder;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.context.properties.SqlLogProperties;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.database.impl.MysqlSqlExecutor;
import com.dynamic.sql.core.database.impl.OracleSqlExecutor;
import com.dynamic.sql.core.database.parser.AbstractDialectParser;
import com.dynamic.sql.core.database.parser.dialect.MysqlParser;
import com.dynamic.sql.core.database.parser.dialect.OracleParser;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.datasource.DataSourceMeta;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.datasource.connection.ConnectionHolder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.interceptor.ExecutionControl;
import com.dynamic.sql.interceptor.SqlInterceptorChain;
import com.dynamic.sql.plugins.logger.SqlLogContext;
import com.dynamic.sql.plugins.logger.SqlLogger;
import com.dynamic.sql.plugins.logger.impl.DefaultSqlLogger;
import com.dynamic.sql.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Collection;
import java.util.function.Function;

public class SqlExecutionFactory {
    private static final Logger log = LoggerFactory.getLogger(SqlExecutionFactory.class);

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
            case ORACLE:
                return new OracleParser(entityClass, schemaProperties, param, whereCondition);
            default:
                throw new DynamicSqlException("Unsupported dialect: " + sqlDialect);
        }
    }

    public static <R> R executorSql(SqlExecuteType sqlExecuteType,
                                    SqlStatementWrapper sqlStatementWrapper,
                                    Function<SqlExecutor, R> doSqlExecutor) {
        String dataSourceName = sqlStatementWrapper.getDataSourceName();
        //添加拦截器
        SqlInterceptorChain sqlInterceptorChain = SqlInterceptorChain.getInstance();
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        if (dataSourceMeta == null) {
            throw new IllegalStateException(dataSourceName + " data source cannot be found.");
        }
        Connection connection = null;
        Exception exception = null;
        R apply = null;
        PreparedSql preparedSql = null;
        try {
            connection = ConnectionHolder.getConnection(dataSourceMeta.getDataSource());
            ExecutionControl executionControl = sqlInterceptorChain.beforeExecution(sqlStatementWrapper, connection);
            preparedSql = SqlUtils.parsePreparedObject(sqlStatementWrapper);
            if (executionControl == ExecutionControl.PROCEED) {
                apply = applySql(sqlExecuteType, dataSourceName, connection, preparedSql, true, doSqlExecutor);
            } else {
                apply = sqlInterceptorChain.retrieveSkippedResult(sqlStatementWrapper, connection);
            }
        } catch (DynamicSqlException e) {
            exception = e;
            throw e;
        } catch (Exception e) {
            exception = e;
            throw new DynamicSqlException(e);
        } finally {
            try {
                ConnectionHolder.releaseConnection(dataSourceMeta.getDataSource(), connection);
            } finally {
                sqlInterceptorChain.afterExecution(preparedSql, apply, exception);
            }
        }
        return apply;
    }

    @SuppressWarnings("all")
    public static <R> R applySql(SqlExecuteType sqlExecuteType,
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
        SqlLogProperties sqlLogProperties = schemaProperties.getSqlLogProperties();
        SqlLogger sqlLogger = sqlLogProperties.getLogger();
        if (sqlLogger == null) {
            sqlLogger = new DefaultSqlLogger();
            sqlLogProperties.setLogger(sqlLogger);
            log.info("If no SqlLogger implementation is found, DefaultSqlLogger will be used.");
        }
        SqlLogContext ctx = new SqlLogContext(sqlExecuteType, dataSourceName, preparedSql, isIntercepted);
        try {
            sqlLogger.beforeSql(sqlLogProperties, ctx);
        } catch (Exception e) {
            log.error("An exception occurred in the SQL logging plugin before execution.", e);
        }
        ctx.setStartTime(System.currentTimeMillis());
        R rawResult = doSqlExecutor.apply(sqlExecutor);
        ctx.setEndTime(System.currentTimeMillis());
        ctx.setRawResult(rawResult);
        try {
            sqlLogger.afterSql(sqlLogProperties, ctx);
        } catch (Exception e) {
            log.error("An exception occurred in the SQL logging plugin after execution.", e);
        }
        return rawResult;
    }

}
