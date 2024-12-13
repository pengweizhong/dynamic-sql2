package com.dynamic.sql.core.dml.delete;

import com.dynamic.sql.context.SchemaContextHolder;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.database.parser.AbstractDialectParser;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntitiesDeleter {
    protected static final Logger log = LoggerFactory.getLogger(EntitiesDeleter.class);
    private Class<?> entityClass;

    public <T> EntitiesDeleter(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    private AbstractDialectParser getDialectParser(Collection<?> params) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + entityClass.getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        return SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, (Collection<Object>) params);
    }

    public int deleteByPrimaryKey(Object pkValue, Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(Collections.singletonList(pkValue));
        dialectParser.deleteByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.DELETE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }

    public int deleteByPrimaryKey(Collection<?> pkValues, Function<SqlExecutor, Integer> doSqlExecutor) {
        AbstractDialectParser dialectParser = getDialectParser(pkValues);
        dialectParser.deleteByPrimaryKey();
        return SqlExecutionFactory.executorSql(DMLType.DELETE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }


    public int delete(Consumer<WhereCondition> condition, Function<SqlExecutor, Integer> doSqlExecutor) {
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
        SchemaProperties.PrintSqlProperties printSqlProperties = schemaProperties.getPrintSqlProperties();
        if (printSqlProperties.isPrintSql() && condition == null) {
            log.warn("When the Where condition is null, the data in the entire table will be deleted");
        }
        AbstractDialectParser dialectParser =
                SqlExecutionFactory.chosenDialectParser(schemaProperties, entityClass, null, whereCondition);
        dialectParser.delete();
        return SqlExecutionFactory.executorSql(DMLType.DELETE, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
    }
}
