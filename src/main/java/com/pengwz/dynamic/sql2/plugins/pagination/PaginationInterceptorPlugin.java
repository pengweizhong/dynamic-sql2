package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptor;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.pengwz.dynamic.sql2.enums.SqlDialect.MYSQL;

public class PaginationInterceptorPlugin implements SqlInterceptor {
//    private static final Logger log = LoggerFactory.getLogger(PaginationPlugin.class);

    @Override
    public boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(sqlStatementWrapper.getDataSourceName());
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                executeMysqlPaging(sqlStatementWrapper, connection);
                break;
            case ORACLE:
                executeOraclePaging(sqlStatementWrapper, connection);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported sql dialect: " + sqlDialect);
        }
        return true;
    }

    private void executeMysqlPaging(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        AbstractPage abstractPage = LocalPage.getCurrentPage();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        // 计算分页的偏移量 (pageIndex - 1) * pageSize
        int offset = (abstractPage.getPageIndex() - 1) * abstractPage.getPageSize();
        String pageSizeKey = parameterBinder.registerValueWithKey(abstractPage.getPageSize());
        String offsetKey = parameterBinder.registerValueWithKey(offset);
        //修改原来的SQL，加上limit分页
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        rawSql.insert(0, "select * from (");
        // 最后加上分页的 LIMIT 子句
        rawSql.append(") _temp_page_table limit ");
        rawSql.append(offsetKey);
        rawSql.append(", ");
        rawSql.append(pageSizeKey);
        // 查询总数量
        DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(sqlStatementWrapper.getDataSourceName());
        PreparedSql preparedSql = SqlUtils.parsePreparedObject(rawSql, parameterBinder);
        //TODO
        SqlExecutionFactory.applySql(connection, MYSQL, preparedSql, SqlExecutor::executeQuery);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {


        } finally {
            SqlUtils.close(resultSet, preparedStatement);
        }

    }

    private void executeOraclePaging(SqlStatementWrapper sqlStatementWrapper, Connection connection) {

    }


    @Override
    public void afterExecution(PreparedSql preparedSql, Exception exception) {
        LocalPage.remove();
    }

}
