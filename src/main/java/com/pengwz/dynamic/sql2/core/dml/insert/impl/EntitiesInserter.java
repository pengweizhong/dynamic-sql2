package com.pengwz.dynamic.sql2.core.dml.insert.impl;

import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.impl.MysqlSqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.enums.DMLType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EntitiesInserter {
    private final List<Object> entities;

    public EntitiesInserter(Object entity) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
    }

    public EntitiesInserter(List<Object> entities) {
        this.entities = entities;
    }

    public int wrapperInsert(Function<SqlExecutor, Integer> doSqlExecutor) {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(null, null, null);
        MysqlSqlExecutor mysqlSqlExecutor = new MysqlSqlExecutor(null, null);
        mysqlSqlExecutor.insertSelective();
        SqlExecutionFactory.executorSql(DMLType.INSERT, sqlStatementWrapper, doSqlExecutor);
        return 0;
    }

}
