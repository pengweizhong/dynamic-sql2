package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

public abstract class AbstractDialectParser implements InsertParser, DeleteParser, UpdateParser {
    protected TableMeta tableMeta;
    protected SchemaProperties schemaProperties;
    protected WhereCondition whereCondition;

    protected AbstractDialectParser(Class<?> entityClass, SchemaProperties schemaProperties, WhereCondition whereCondition) {
        this.schemaProperties = schemaProperties;
        tableMeta = TableProvider.getTableMeta(entityClass);
        this.whereCondition = whereCondition;
    }

    public abstract SqlStatementWrapper getSqlStatementWrapper();


}
