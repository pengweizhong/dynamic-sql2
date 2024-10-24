package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.impl.MysqlSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.impl.OracleSqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.datasource.ConnectionHolder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SqlExecutionFactory {
    private SqlExecutionFactory() {
    }

    private static final Logger log = LoggerFactory.getLogger(SqlExecutionFactory.class);

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
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        try {
            connection = ConnectionHolder.getConnection(dataSourceMeta.getDataSource());
            boolean beforeExecution = sqlInterceptorChain.beforeExecution(sqlStatementWrapper, connection);
            preparedSql = SqlUtils.parsePreparedObject(rawSql, parameterBinder);
            if (beforeExecution) {
                apply = applySql(dmlType, dataSourceName, connection, preparedSql, beforeExecution, doSqlExecutor);
            }
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                ConnectionHolder.releaseConnection(connection);
            } finally {
                sqlInterceptorChain.afterExecution(preparedSql, exception);
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
                throw new UnsupportedOperationException("Unsupported sql dialect: " + schemaProperties.getSqlDialect());
        }
        if (schemaProperties.isPrintSql()) {
            //输出编译后的SQL
            StringBuilder stringBuilder = new StringBuilder();
            List<Object> params = preparedSql.getParams();
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                stringBuilder.append(param).append("(").append(param.getClass().getSimpleName()).append(")");
                if (i != params.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            log.debug("{} -->     Preparing: {}", dataSourceName, preparedSql.getSql());
            log.debug("{} -->    Parameters: {}", dataSourceName, stringBuilder);
            if (!isIntercepted) {
                log.debug("{} -->       !!!!!! : SQL is intercepted.", dataSourceName);
            }
        }
        R applyResult = doSqlExecutor.apply(sqlExecutor);
        if (schemaProperties.isPrintSql()) {
            if (dmlType == DMLType.SELECT) {
                if (applyResult instanceof Collection) {
                    Collection collection = (Collection) applyResult;
                    log.debug("{} <--         Total: {}", dataSourceName, collection.size());
                } else {
                    log.debug("{} <--     Returned: {}", dataSourceName, applyResult);
                }
            }
            if (dmlType == DMLType.INSERT || dmlType == DMLType.UPDATE || dmlType == DMLType.DELETE) {
                log.debug("{} <-- Affected Rows: {}", dataSourceName, applyResult);
            }
        }
        return applyResult;
    }

}
