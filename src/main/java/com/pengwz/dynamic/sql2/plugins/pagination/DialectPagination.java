package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

public interface DialectPagination {

    StringBuilder selectCountSql(Version version, SqlStatementWrapper sqlStatementWrapper);

    void modifyPagingSql(Version version,
                         SqlStatementWrapper sqlStatementWrapper,
                         String offsetKey,
                         String pageSizeKey);
}
