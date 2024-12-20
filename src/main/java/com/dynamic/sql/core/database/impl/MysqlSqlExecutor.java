package com.dynamic.sql.core.database.impl;

import com.dynamic.sql.core.database.AbstractSqlExecutor;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.database.RootExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MysqlSqlExecutor extends AbstractSqlExecutor {
    private static final Logger log = LoggerFactory.getLogger(MysqlSqlExecutor.class);

    public MysqlSqlExecutor(Connection connection, PreparedSql preparedSql) {
        super(connection, preparedSql);
    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return RootExecutor.executeQuery(connection, preparedSql);
    }

    @Override
    public int insertSelective() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int insert() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int insertBatch() {
        return RootExecutor.executeInsertBatch(connection, preparedSql);
    }

    @Override
    public int insertMultiple() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int deleteByPrimaryKey() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int delete() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int updateByPrimaryKey() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int updateSelectiveByPrimaryKey() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int update() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int updateSelective() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int upsert() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int upsertSelective() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int upsertMultiple() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }
}
