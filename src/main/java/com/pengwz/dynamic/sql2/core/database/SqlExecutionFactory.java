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
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.function.Function;

public class SqlExecutionFactory {
    private SqlExecutionFactory() {
    }

    private static final Logger log = LoggerFactory.getLogger(SqlExecutionFactory.class);

    public static <R> R executorSql(SqlStatementWrapper sqlStatementWrapper, Function<SqlExecutor, R> doSqlExecutor) {
        String dataSourceName = sqlStatementWrapper.getDataSourceName();
        //添加拦截器
        SqlInterceptorChain sqlInterceptorChain = SqlInterceptorChain.getInstance();
        boolean beforeExecution = sqlInterceptorChain.beforeExecution(sqlStatementWrapper);
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        PreparedSql preparedSql = SqlUtils.parsePreparedObject(rawSql, parameterBinder);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        //输出编译后的SQL
        if (schemaProperties.isPrintSql()) {
            log.debug("{} -> {}", dataSourceName, preparedSql.getSql());
        }
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
        Connection connection = null;
        Exception exception = null;
        R apply = null;
        try {
            if (beforeExecution) {
                connection = ConnectionHolder.getConnection(dataSourceMeta.getDataSource());
                SqlExecutor sqlExecutor;
                switch (sqlDialect) {
                    case MYSQL:
                        sqlExecutor = new MysqlSqlExecutor(connection, preparedSql);
                        break;
                    case ORACLE:
                        sqlExecutor = new OracleSqlExecutor(connection, preparedSql);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported sql dialect: " + sqlDialect);
                }
                apply = doSqlExecutor.apply(sqlExecutor);
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

}
