package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
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
    private final Fn<?, ?>[] forcedFields;

    public EntitiesUpdater(Collection<Object> entities) {
        this.entities = entities;
        this.entityClass = entities.iterator().next().getClass();
        this.forcedFields = null;
    }

    public EntitiesUpdater(Collection<Object> entities, Fn<?, ?>[] forcedFields) {
        this.entities = entities;
        this.entityClass = entities.iterator().next().getClass();
        this.forcedFields = forcedFields;
    }

    public int updateByPrimaryKey(Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(entities);
        dialectParser.updateByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    public int updateSelectiveByPrimaryKey(Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(entities);
        dialectParser.updateSelectiveByPrimaryKey(forcedFields);
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    private AbstractDialectParser getDialectParser(Collection<Object> params) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        return SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, params);
    }

}
