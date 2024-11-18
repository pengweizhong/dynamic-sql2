package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.Collections;
import java.util.function.Function;

public class EntitiesDeleter {
    private Class<?> entityClass;

    public <T> EntitiesDeleter(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    private AbstractDialectParser getDialectParser(Object param) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        return SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, Collections.singletonList(param));
    }

    public int deleteByPrimaryKey(Object pkValue, Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(pkValue);
        dialectParser.deleteByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }


}
