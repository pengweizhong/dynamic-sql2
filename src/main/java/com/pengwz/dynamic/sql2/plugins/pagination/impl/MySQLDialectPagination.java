package com.pengwz.dynamic.sql2.plugins.pagination.impl;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.plugins.pagination.DialectPagination;

import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class MySQLDialectPagination implements DialectPagination {
    @Override
    public StringBuilder selectCountSql(Version version, SqlStatementWrapper sqlStatementWrapper) {
        StringBuilder selectCountSql = new StringBuilder(sqlStatementWrapper.getRawSql());
        selectCountSql.insert(0, "select count(1) from (");
        selectCountSql.append(") _count_page_temp ");
        return selectCountSql;
    }

    @Override
    public void modifyPagingSql(Version version, SqlStatementWrapper sqlStatementWrapper, int pageIndex, int pageSize) {
        //修改原来的SQL，加上limit分页
        StringBuilder selectPageSql = sqlStatementWrapper.getRawSql();
        selectPageSql.insert(0, "select * from (");
        // 最后加上分页的 LIMIT 子句
        selectPageSql.append(") _page_temp limit ");
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        // 计算分页的偏移量 (pageIndex - 1) * pageSize
        int offset = (pageIndex - 1) * pageSize;
        String offsetKey = registerValueWithKey(parameterBinder, offset);
        String pageSizeKey = registerValueWithKey(parameterBinder, pageSize);
        selectPageSql.append(offsetKey);
        selectPageSql.append(", ");
        selectPageSql.append(pageSizeKey);
    }
}
