package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

public abstract class AbstractDialectParser implements InsertParser, DeleteParser {
    protected SchemaProperties schemaProperties;

    protected AbstractDialectParser(SchemaProperties schemaProperties) {
        this.schemaProperties = schemaProperties;
    }

    public abstract SqlStatementWrapper getSqlStatementWrapper();


}
