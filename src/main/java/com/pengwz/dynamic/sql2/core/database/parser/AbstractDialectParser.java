package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.dml.select.QueryHandler;

public abstract class AbstractDialectParser implements InsertParser, QueryHandler {
    protected SchemaProperties schemaProperties;

    protected AbstractDialectParser(SchemaProperties schemaProperties) {
        this.schemaProperties = schemaProperties;
    }

    public abstract SqlStatementWrapper getSqlStatementWrapper();


}
