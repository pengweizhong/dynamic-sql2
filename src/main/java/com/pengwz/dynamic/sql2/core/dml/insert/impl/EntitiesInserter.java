package com.pengwz.dynamic.sql2.core.dml.insert.impl;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EntitiesInserter {
    private final Collection<Object> entities;
    private AbstractDialectParser dialectParser;
    private final Fn<?, ?>[] forcedFields;

    public EntitiesInserter(Object entity, Fn<?, ?>[] forcedFields) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
        this.forcedFields = forcedFields;
        init();
    }

    public EntitiesInserter(Collection<Object> entities) {
        this.entities = entities;
        this.forcedFields = null;
        init();
    }

    private void init() {
        Object next = entities.iterator().next();
        TableMeta tableMeta = TableProvider.getTableMeta(next.getClass());
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        dialectParser = SqlExecutionFactory.chosenDialectParser(schemaProperties, entities);
    }

    public int insertSelective(Function<SqlExecutor, Integer> doSqlExecutor) {
        dialectParser.insertSelective(forcedFields);
        return SqlExecutionFactory.executorSql(DMLType.INSERT, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    public int insert(Function<SqlExecutor, Integer> doSqlExecutor) {
        dialectParser.insert();
        return SqlExecutionFactory.executorSql(DMLType.INSERT, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }
}
