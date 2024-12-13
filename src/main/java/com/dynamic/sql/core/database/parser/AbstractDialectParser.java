package com.dynamic.sql.core.database.parser;


import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;

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