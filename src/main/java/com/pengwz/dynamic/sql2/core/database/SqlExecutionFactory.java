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
import java.util.List;
import java.util.function.Function;

public class SqlExecutionFactory {
    private SqlExecutionFactory() {
    }

    private static final Logger log = LoggerFactory.getLogger(SqlExecutionFactory.class);

    public static <R> R executorSql(SqlStatementWrapper sqlStatementWrapper, Function<SqlExecutor, R> doSqlExecutor) {
        Connection connection = null;
        Exception exception = null;
        R apply = null;
        String dataSourceName = sqlStatementWrapper.getDataSourceName();
        //添加拦截器
        SqlInterceptorChain sqlInterceptorChain = SqlInterceptorChain.getInstance();
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        PreparedSql preparedSql = SqlUtils.parsePreparedObject(rawSql, parameterBinder);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        boolean beforeExecution = sqlInterceptorChain.beforeExecution(sqlStatementWrapper);
        printSql(dataSourceName, schemaProperties, preparedSql);
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
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

    private static void printSql(String dataSourceName, SchemaProperties schemaProperties, PreparedSql preparedSql) {
        //输出编译后的SQL
        if (schemaProperties.isPrintSql()) {
            StringBuilder stringBuilder = new StringBuilder();
            List<Object> params = preparedSql.getParams();
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                stringBuilder.append(param).append("(").append(param.getClass().getSimpleName()).append(")");
                if (i != params.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            log.debug("{} -> Preparing: {}", dataSourceName, preparedSql.getSql());
            log.debug("{} -> Parameters: {}", dataSourceName, stringBuilder);
        }
    }

}
