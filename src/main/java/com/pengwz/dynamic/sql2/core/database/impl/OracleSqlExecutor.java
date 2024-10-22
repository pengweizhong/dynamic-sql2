package com.pengwz.dynamic.sql2.core.database.impl;

import com.pengwz.dynamic.sql2.core.database.AbstractSqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

import java.sql.Connection;
import java.util.List;
import java.util.Map;


public class OracleSqlExecutor extends AbstractSqlExecutor {

    public OracleSqlExecutor(Connection connection, SqlStatementWrapper sqlStatementWrapper) {
        super(connection, sqlStatementWrapper);
    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return null;
    }
}
