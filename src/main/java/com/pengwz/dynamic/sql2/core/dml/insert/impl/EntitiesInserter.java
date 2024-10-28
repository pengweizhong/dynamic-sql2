package com.pengwz.dynamic.sql2.core.dml.insert.impl;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EntitiesInserter {
    private final List<Object> entities;
    private AbstractDialectParser dialectParser;

    public EntitiesInserter(Object entity) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
        init();
    }

    public EntitiesInserter(List<Object> entities) {
        this.entities = entities;
        init();
    }

    private void init() {
        TableMeta tableMeta = TableProvider.getTableMeta(entities.get(0).getClass());
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        dialectParser = SqlExecutionFactory.chosenDialectParser(schemaProperties, entities);
    }

    public int insertSelective(Function<SqlExecutor, Integer> doSqlExecutor) {
        dialectParser.insertSelective();
        return SqlExecutionFactory.executorSql(DMLType.INSERT, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

}
