package com.pengwz.dynamic.sql2.plugins.pagination.impl;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.plugins.pagination.DialectPagination;

public class MySQLDialectPagination implements DialectPagination {
    @Override
    public StringBuilder selectCountSql(Version version, SqlStatementWrapper sqlStatementWrapper) {
        StringBuilder selectCountSql = new StringBuilder(sqlStatementWrapper.getRawSql());
        selectCountSql.insert(0, "select count(1) from (");
        selectCountSql.append(") _count_page_temp ");
        return selectCountSql;
    }

    @Override
    public void modifyPagingSql(Version version, SqlStatementWrapper sqlStatementWrapper, String offsetKey, String pageSizeKey) {
        //修改原来的SQL，加上limit分页
        StringBuilder selectPageSql = sqlStatementWrapper.getRawSql();
        selectPageSql.insert(0, "select * from (");
        // 最后加上分页的 LIMIT 子句
        selectPageSql.append(") _page_temp limit ");
        selectPageSql.append(offsetKey);
        selectPageSql.append(", ");
        selectPageSql.append(pageSizeKey);
    }
}
