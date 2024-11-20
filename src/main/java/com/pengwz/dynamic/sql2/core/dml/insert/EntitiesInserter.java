package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.database.parser.AbstractDialectParser;
import com.pengwz.dynamic.sql2.core.database.parser.InsertParser;
import com.pengwz.dynamic.sql2.enums.DMLType;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntitiesInserter {
    private static final ThreadLocal<Collection<Object>> LOCAL_ENTITIES = new ThreadLocal<>();
    private AbstractDialectParser dialectParser;
    private final Fn<?, ?>[] forcedFields;

    public EntitiesInserter(Object entity, Fn<?, ?>[] forcedFields) {
        this.forcedFields = forcedFields;
        setEntities(Collections.singleton(entity));
        init();
    }

    public EntitiesInserter(Collection<Object> entities) {
        this.forcedFields = null;
        setEntities(entities);
        init();
    }

    private void init() {
        Object next = getLocalEntities().iterator().next();
        TableMeta tableMeta = TableProvider.getTableMeta(next.getClass());
        if (tableMeta == null) {
            throw new IllegalStateException("Class `" + next.getClass().getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL");
        }
        String dataSourceName = tableMeta.getBindDataSourceName();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        dialectParser = SqlExecutionFactory.chosenDialectParser(schemaProperties, next.getClass(), getLocalEntities());
    }

    /**
     * 模板方法，通过指定的SQL构建过程和执行器完成SQL操作。
     *
     * @param dmlType       DML操作类型
     * @param sqlAction     SQL语句的构建过程
     * @param doSqlExecutor SQL执行的具体实现
     * @return 受影响的行数
     */
    private int execute(DMLType dmlType,
                        Consumer<AbstractDialectParser> sqlAction,
                        Function<SqlExecutor, Integer> doSqlExecutor) {
        try {
            sqlAction.accept(dialectParser);
            return SqlExecutionFactory.executorSql(dmlType, dialectParser.getSqlStatementWrapper(), doSqlExecutor);
        } finally {
            clearEntities();
        }

    }

    /**
     * 执行带有选择性字段的插入。
     *
     * @param doSqlExecutor SQL执行器
     * @return 受影响的行数
     */
    public int insertSelective(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.INSERT, parser -> parser.insertSelective(forcedFields), doSqlExecutor);
    }

    /**
     * 执行普通插入操作。
     *
     * @param doSqlExecutor SQL执行器
     * @return 受影响的行数
     */
    public int insert(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.INSERT, AbstractDialectParser::insert, doSqlExecutor);
    }

    /**
     * 执行批量插入操作。
     *
     * @param doSqlExecutor SQL执行器
     * @return 受影响的行数
     */
    public int insertBatch(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.INSERT, AbstractDialectParser::insertBatch, doSqlExecutor);
    }

    public int insertMultiple(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.INSERT, AbstractDialectParser::insertMultiple, doSqlExecutor);
    }

    public int upsert(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.UPSERT, parser -> parser.upsert(forcedFields), doSqlExecutor);
    }

    public int upsertSelective(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.UPSERT, parser -> parser.upsertSelective(forcedFields), doSqlExecutor);
    }

    public int upsertMultiple(Function<SqlExecutor, Integer> doSqlExecutor) {
        return execute(DMLType.UPSERT, InsertParser::upsertMultiple, doSqlExecutor);
    }

    private void setEntities(Collection<Object> entities) {
        LOCAL_ENTITIES.set(entities);
    }

    public static Collection<Object> getLocalEntities() {
        return LOCAL_ENTITIES.get();
    }

    private void clearEntities() {
        LOCAL_ENTITIES.remove();
    }
}
