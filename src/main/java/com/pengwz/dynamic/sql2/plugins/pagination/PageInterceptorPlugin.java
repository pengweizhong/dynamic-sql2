package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.interceptor.ExecutionControl;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptor;
import com.pengwz.dynamic.sql2.plugins.pagination.impl.MySQLDialectPagination;
import com.pengwz.dynamic.sql2.plugins.pagination.impl.OracleDialectPagination;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class PageInterceptorPlugin implements SqlInterceptor {
//    private static final Logger log = LoggerFactory.getLogger(PaginationPlugin.class);

    @Override
    public ExecutionControl beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        AbstractPage currentPage = LocalPage.getCurrentPage();
        //没有分页参数，直接跳过
        if (currentPage == null) {
            return ExecutionControl.PROCEED;
        }
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(sqlStatementWrapper.getDataSourceName());
        Version version = new Version(schemaProperties.getMajorVersionNumber(),
                schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
        DialectPagination dialectPagination;
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                dialectPagination = new MySQLDialectPagination();
                break;
            case ORACLE:
                dialectPagination = new OracleDialectPagination();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported sql dialect: " + sqlDialect);
        }
        AbstractPage abstractPage = LocalPage.getCurrentPage();
        Long total = abstractPage.getCacheTotal();
        if (total == null) {
            total = executeCountSql(sqlStatementWrapper, connection,
                    dialectPagination.selectCountSql(version, sqlStatementWrapper));
            currentPage.setTotal(total);
            currentPage.initTotalPage();
        }
        //没有数据就没有必要继续执行
        if (total == 0) {
            return ExecutionControl.SKIP;
        }
        //如果超出了查询范围，也没有必要执行
        if (abstractPage.getPageIndex() > abstractPage.getTotalPage()) {
            return ExecutionControl.SKIP;
        }
        dialectPagination.modifyPagingSql(version, sqlStatementWrapper, abstractPage.getPageIndex(), abstractPage.getPageSize());
        return ExecutionControl.PROCEED;
    }

    @Override
    public void afterExecution(PreparedSql preparedSql, Object applyResult, Exception exception) {

    }

    private long executeCountSql(SqlStatementWrapper sqlStatementWrapper, Connection connection, StringBuilder countSql) {
        PreparedSql preparedSql = SqlUtils.parsePreparedObject(countSql, sqlStatementWrapper.getParameterBinder());
        List<Map<String, Object>> resultCountList = SqlExecutionFactory.applySql(DMLType.SELECT,
                sqlStatementWrapper.getDataSourceName(), connection,
                preparedSql, true, SqlExecutor::executeQuery);
        if (resultCountList.isEmpty()) {
            return 0;
        }
        Map.Entry<String, Object> countMap = resultCountList.get(0).entrySet().iterator().next();
        return Long.parseLong(countMap.getValue().toString());
    }

}
