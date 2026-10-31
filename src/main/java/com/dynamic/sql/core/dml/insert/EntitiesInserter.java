package com.dynamic.sql.core.dml.insert;


import com.dynamic.sql.context.SchemaContextHolder;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.database.parser.AbstractDialectParser;
import com.dynamic.sql.core.database.parser.InsertParser;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.GenerationType;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.GeneratedStrategy;
import com.dynamic.sql.plugins.id.IdGenerators;
import com.dynamic.sql.plugins.id.IdGenerationContext;
import com.dynamic.sql.plugins.id.SnowflakeIdGenerator;
import com.dynamic.sql.utils.ConverterUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;

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
            throw new DynamicSqlException("Class `" + next.getClass().getCanonicalName()
                    + "` is not managed or cached by Dynamic-SQL, or it inserts data into a non-physical table.");
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
            //在 SQL 解析前统一补应用侧主键，普通、选择性、批量及 upsert 共用入口。
            generatePrimaryKeys();
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

    /** 只填空主键，显式业务主键始终保留；数据库 AUTO/SEQUENCE 不进入应用生成路径。 */
    private void generatePrimaryKeys() {
        for(Object entity:getLocalEntities()) {
            TableMeta table=TableProvider.getTableMeta(entity.getClass());
            for(ColumnMeta column:table.<ColumnMeta>getColumnMetas()) {
                GeneratedStrategy strategy=column.getGeneratedStrategy();
                if(strategy==null || (strategy.getStrategy()!=GenerationType.SNOWFLAKE && strategy.getStrategy()!=GenerationType.CUSTOM))
                    continue;
                try {
                    column.getField().setAccessible(true);
                    if(column.getField().get(entity)!=null) continue;
                    if(strategy.getStrategy()==GenerationType.SNOWFLAKE
                            && column.getField().getType()!=Long.class && column.getField().getType()!=String.class)
                        throw new IllegalArgumentException("Snowflake ID field must be Long or String");
                    String name=strategy.getGenerator();
                    if(name==null || name.trim().isEmpty()) {
                        if(strategy.getStrategy()==GenerationType.CUSTOM) throw new IllegalArgumentException("Custom ID generator name is required");
                        name="snowflake";
                    }
                    if(strategy.getStrategy()==GenerationType.SNOWFLAKE && !(IdGenerators.get(name) instanceof SnowflakeIdGenerator))
                        throw new IllegalArgumentException("Snowflake strategy requires a SnowflakeIdGenerator");
                    Object generated=IdGenerators.get(name).generate(new IdGenerationContext(entity,column,table.getBindDataSourceName()));
                    if(generated==null) throw new IllegalStateException("ID generator returned null");
                    Object converted=ConverterUtils.convertToEntityAttribute(column,column.getField().getType(),generated);
                    ReflectUtils.setFieldValue(entity,column.getField(),converted);
                } catch(IllegalAccessException error) { throw new DynamicSqlException(error); }
            }
        }
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
