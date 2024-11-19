package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.Collection;
import java.util.function.Function;

public class EntitiesUpdater {
    private final Collection<Object> entities;
    private Class<?> entityClass;

    public EntitiesUpdater(Collection<Object> entities) {
        this.entities = entities;
        this.entityClass = entities.iterator().next().getClass();
    }

    public int updateByPrimaryKey(Function<SqlExecutor, Integer> doSqlExecutor) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        AbstractDialectParser dialectParser = SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, entities);
        dialectParser.updateByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }
}
