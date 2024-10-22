package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.LogProperties;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.impl.MysqlSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.impl.OracleSqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.datasource.ConnectionHolder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.sql.Connection;
import java.util.function.Function;

public class SqlExecutionFactory {
    private SqlExecutionFactory() {
    }

    public static <R> R executorSql(SqlStatementWrapper sqlStatementWrapper, Function<SqlExecutor, R> doSqlExecutor) {
        String dataSourceName = sqlStatementWrapper.getDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        if (schemaProperties.isPrintSql()) {
            SqlLogger sqlLogger = LogProperties.getInstance().getSqlLogger();
            sqlLogger.logSql(sqlStatementWrapper.getDataSourceName(), SqlUtils.replacePlaceholdersWithValues(sqlStatementWrapper));
        }
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(dataSourceName);
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
        Connection connection = null;
        try {
            connection = ConnectionHolder.getConnection(dataSourceMeta.getDataSource());
            SqlExecutor sqlExecutor;
            switch (sqlDialect) {
                case MYSQL:
                    sqlExecutor = new MysqlSqlExecutor(connection, sqlStatementWrapper);
                    break;
                case ORACLE:
                    sqlExecutor = new OracleSqlExecutor(connection, sqlStatementWrapper);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported sql dialect: " + sqlDialect);
            }
            return doSqlExecutor.apply(sqlExecutor);
        } finally {
            ConnectionHolder.releaseConnection(connection);
        }

    }

}
