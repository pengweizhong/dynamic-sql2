package com.pengwz.dynamic.sql2.plugins.pagination.impl;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.plugins.pagination.DialectPagination;

import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class OracleDialectPagination implements DialectPagination {
    @Override
    public StringBuilder selectCountSql(Version version, SqlStatementWrapper sqlStatementWrapper) {
        StringBuilder selectCountSql = new StringBuilder(sqlStatementWrapper.getRawSql());
        selectCountSql.insert(0, "SELECT COUNT(1) FROM (");
        selectCountSql.append(") COUNT_PAGE_TEMP ");
        return selectCountSql;
    }

    @Override
    public void modifyPagingSql(Version version, SqlStatementWrapper sqlStatementWrapper, int pageIndex, int pageSize) {
        //在 Oracle 12c 及以上版本，分页的逻辑得到了显著简化，新增了 SQL 标准中的 FETCH FIRST 和 OFFSET 子句，可以更优雅地实现分页
        if (version.isGreaterThanOrEqual(new Version(12, 0, 0))) {
            offsetAndFetch(sqlStatementWrapper, pageIndex, pageSize);
            return;
        }
        rowNumPage(sqlStatementWrapper, pageIndex, pageSize);
    }

    private void offsetAndFetch(SqlStatementWrapper sqlStatementWrapper, int pageIndex, int pageSize) {
        //分页公式：
        //OFFSET = (pageIndex - 1) * pageSize
        int offset = (pageIndex - 1) * pageSize;
        //FETCH NEXT = pageSize
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        String offsetKey = registerValueWithKey(parameterBinder, offset);
        String pageSizeKey = registerValueWithKey(parameterBinder, pageSize);
        StringBuilder pagingSql = sqlStatementWrapper.getRawSql();
        pagingSql.insert(0, "SELECT * FROM (");
        pagingSql.append(") OFFSET ")
                .append(offsetKey)
                .append(" ROWS FETCH NEXT ")
                .append(pageSizeKey)
                .append(" ROWS ONLY");
    }

    private void rowNumPage(SqlStatementWrapper sqlStatementWrapper, int pageIndex, int pageSize) {
        //内部ROWNUM  页码 * 每页行数
        int rowNum = pageIndex * pageSize;
        //外部限制范围 (页码 - 1) * 每页行数
        int rn = (pageIndex - 1) * pageSize;
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        String offsetKey = registerValueWithKey(parameterBinder, rowNum);
        String pageSizeKey = registerValueWithKey(parameterBinder, rn);
        //将原始SQL包装成子查询
        // 插入分页逻辑
        StringBuilder pagingSql = sqlStatementWrapper.getRawSql();
        pagingSql.insert(0, "SELECT * FROM (SELECT PAGE_TEMP.*, ROWNUM RN FROM (");
        pagingSql.append(") PAGE_TEMP ");
        pagingSql.append("WHERE ROWNUM <= ").append(offsetKey);
        pagingSql.append(") WHERE RN > ").append(pageSizeKey);
    }
}
