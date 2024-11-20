package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntitiesUpdater {
    protected static final Logger log = LoggerFactory.getLogger(EntitiesUpdater.class);
    private final Collection<Object> entities;
    private Class<?> entityClass;
    private final Fn<?, ?>[] forcedFields;
    private final Consumer<WhereCondition> condition;

    public EntitiesUpdater(Collection<Object> entities) {
        this.entities = entities;
        this.entityClass = entities.iterator().next().getClass();
        this.forcedFields = null;
        this.condition = null;
    }

    public EntitiesUpdater(Collection<Object> entities, Fn<?, ?>[] forcedFields) {
        this.entities = entities;
        this.entityClass = entities.iterator().next().getClass();
        this.forcedFields = forcedFields;
        this.condition = null;
    }

    public EntitiesUpdater(Collection<Object> entities, Fn<?, ?>[] forcedFields, Consumer<WhereCondition> condition) {
        this.entities = entities;
        this.entityClass = entities.iterator().next().getClass();
        this.forcedFields = forcedFields;
        this.condition = condition;
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

    public int update(Function<SqlExecutor, Integer> doSqlExecutor) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        WhereCondition whereCondition = null;
        if (condition != null) {
            Version version = new Version(schemaProperties.getMajorVersionNumber(),
                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
            whereCondition = SqlUtils.matchDialectCondition(schemaProperties.getSqlDialect(),
                    version, null, dataSourceName);
            condition.accept(whereCondition);
        }
        if (schemaProperties.isPrintSql() && condition == null) {
            log.warn("When the Where condition is null, the data in the entire table will be updated");
        }
        AbstractDialectParser dialectParser =
                SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, entities, whereCondition);
        dialectParser.update();
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    public int updateSelective(Function<SqlExecutor, Integer> doSqlExecutor) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        WhereCondition whereCondition = null;
        if (condition != null) {
            Version version = new Version(schemaProperties.getMajorVersionNumber(),
                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
            whereCondition = SqlUtils.matchDialectCondition(schemaProperties.getSqlDialect(),
                    version, null, dataSourceName);
            condition.accept(whereCondition);
        }
        if (schemaProperties.isPrintSql() && condition == null) {
            log.warn("When the Where condition is null, the data in the entire table will be updated");
        }
        AbstractDialectParser dialectParser =
                SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, entities, whereCondition);
        dialectParser.updateSelective(forcedFields);
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    public int upsert(Function<SqlExecutor, Integer> doSqlExecutor) {
        return 0;
    }
}
