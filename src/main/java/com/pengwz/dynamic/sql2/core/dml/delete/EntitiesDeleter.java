package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.core.dml.select.build.WhereCondition;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntitiesDeleter {
    private Class<?> entityClass;

    public <T> EntitiesDeleter(Class<T> entityClass) {
        this.entityClass = entityClass;
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

    public int deleteByPrimaryKey(Object pkValue, Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(Collections.singletonList(pkValue));
        dialectParser.deleteByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    public int deleteByPrimaryKey(Collection<Object> pkValues, Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(pkValues);
        dialectParser.deleteByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }


    public int delete(Consumer<WhereCondition> condition, Function<SqlExecutor, Integer> doSqlExecutor) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
//        AbstractDialectParser dialectParser = SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, params);
        Version version = new Version(schemaProperties.getMajorVersionNumber(),
                schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
        WhereCondition whereCondition = SqlUtils.matchDialectCondition(schemaProperties.getSqlDialect(),
                version, null, dataSourceName);
        condition.accept(whereCondition);

        return 0;
    }
}
