package com.pengwz.dynamic.sql2.interceptor;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageSqlInterceptor implements SqlInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PageSqlInterceptor.class);

    @Override
    public boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper) {
        return true;
    }

    @Override
    public void afterExecution(PreparedSql preparedSql, Exception exception) {

    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
