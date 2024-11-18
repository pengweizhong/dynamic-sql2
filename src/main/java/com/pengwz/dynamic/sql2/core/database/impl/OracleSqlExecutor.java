package com.pengwz.dynamic.sql2.core.database.impl;

import com.pengwz.dynamic.sql2.core.database.AbstractSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.PreparedSql;

import java.sql.Connection;
import java.util.List;
import java.util.Map;


public class OracleSqlExecutor extends AbstractSqlExecutor {

    public OracleSqlExecutor(Connection connection, PreparedSql preparedSql) {
        super(connection, preparedSql);
    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return null;
    }

    @Override
    public int insertSelective() {
        return 0;
    }

    @Override
    public int insert() {
        return 0;
    }

    @Override
    public int insertBatch() {
        return 0;
    }

    @Override
    public int insertMultiple() {
        return 0;
    }

    @Override
    public int deleteByPrimaryKey() {
        return 0;
    }
}
