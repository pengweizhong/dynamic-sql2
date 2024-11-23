package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptor;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class PageInterceptorPlugin implements SqlInterceptor {
//    private static final Logger log = LoggerFactory.getLogger(PaginationPlugin.class);

    @Override
    public boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        AbstractPage currentPage = LocalPage.getCurrentPage();
        //没有分页参数，直接跳过
        if (currentPage == null) {
            return true;
        }
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(sqlStatementWrapper.getDataSourceName());
        SqlDialect sqlDialect = schemaProperties.getSqlDialect();
        AbstractPage abstractPage = LocalPage.getCurrentPage();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        // 计算分页的偏移量 (pageIndex - 1) * pageSize
        int offset = (abstractPage.getPageIndex() - 1) * abstractPage.getPageSize();
        String offsetKey = registerValueWithKey(parameterBinder, offset);
        String pageSizeKey = registerValueWithKey(parameterBinder, abstractPage.getPageSize());
        Long total = abstractPage.getCacheTotal();
        if (total == null) {
            total = selectTotal(sqlStatementWrapper, connection);
            currentPage.setTotal(total);
            currentPage.initTotalPage();
        }
        //没有数据就没有必要继续执行
        if (total == 0) {
            return false;
        }
        //如果超出了查询范围，也没有必要执行
        if (abstractPage.getPageIndex() > abstractPage.getTotalPage()) {
            return false;
        }
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                executeMysqlPaging(sqlStatementWrapper, offsetKey, pageSizeKey);
                break;
            case ORACLE:
                executeOraclePaging(sqlStatementWrapper, offsetKey, pageSizeKey);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported sql dialect: " + sqlDialect);
        }
        return true;
    }

    private void executeMysqlPaging(SqlStatementWrapper sqlStatementWrapper,
                                    String offsetKey,
                                    String pageSizeKey) {
        //修改原来的SQL，加上limit分页
        StringBuilder selectPageSql = sqlStatementWrapper.getRawSql();
        selectPageSql.insert(0, "select * from (");
        // 最后加上分页的 LIMIT 子句
        selectPageSql.append(") _page_temp limit ");
        selectPageSql.append(offsetKey);
        selectPageSql.append(", ");
        selectPageSql.append(pageSizeKey);
    }

    private void executeOraclePaging(SqlStatementWrapper sqlStatementWrapper,
                                     String offsetKey,
                                     String pageSizeKey) {
        //TODO
    }


    @Override
    public void afterExecution(PreparedSql preparedSql, Object applyResult, Exception exception) {

    }

    private long selectTotal(SqlStatementWrapper sqlStatementWrapper,
                             Connection connection
                             /*String offsetKey,
                             String pageSizeKey*/) {
        StringBuilder selectCountSql = new StringBuilder(sqlStatementWrapper.getRawSql());
        selectCountSql.insert(0, "select count(1) from (");
        selectCountSql.append(") _count_page_temp ");
//        selectCountSql.append(offsetKey);
//        selectCountSql.append(", ");
//        selectCountSql.append(pageSizeKey);
        PreparedSql preparedSql = SqlUtils.parsePreparedObject(selectCountSql, sqlStatementWrapper.getParameterBinder());

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
