package com.pengwz.dynamic.sql2.core.database.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.database.AbstractSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.database.RootExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Collection;
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
        return 0;
    }

    @Override
    public <T, F> int insertSelective(Collection<Fn<T, F>> forcedFields) {
        return 0;
    }

    @Override
    public int insert() {
        return 0;
    }

    @Override
    public int batchInsert() {
        return 0;
    }
}
