package com.dynamic.sql.core;


import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.model.ColumnMetaData;
import com.dynamic.sql.model.TableMetaData;
import com.dynamic.sql.utils.SqlUtils;

import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * SQL 操作上下文，提供构建和执行 SQL 语句的方法。
 * <p>
 * 该接口定义了一系列方法，用于构建 SQL 查询、插入、更新、删除等操作。
 * </p>
 */
public interface SqlContext {
    /**
     * 创建并返回一个 {@link AbstractColumnReference} 对象，用于构建 SQL 查询操作
     * ，并选择所需的列进行检索。
     *
     * @return 返回一个 {@code AbstractColumnReference} 对象
     */
    AbstractColumnReference select();

    /**
     * 根据主键查询实体对象。
     *
     * @param <T>         实体类的类型。
     * @param entityClass 实体类的 {@link Class} 对象，用于指定查询结果的映射类型。
     * @param pkValue     主键值，用于定位唯一的数据库记录。
     * @return 查询到的实体对象。如果没有匹配的记录，则返回 {@code null}。
     */
    <T> T selectByPrimaryKey(Class<T> entityClass, Object pkValue);

    /**
     * 根据多个主键查询实体对象集合。
     *
     * @param <T>         实体类的类型。
     * @param entityClass 实体类的 {@link Class} 对象，用于指定查询结果的映射类型。
     * @param pkValues    主键值集合
     * @return 查询到的实体对象。如果没有匹配的记录，则返回空集合。
     */
    <T> List<T> selectByPrimaryKey(Class<T> entityClass, Collection<?> pkValues);

    /**
     * 执行指定的 SQL 查询，返回映射为指定类型的单个结果。
     *
     * @param sql        要执行的 SQL 查询语句。
     * @param returnType 结果映射的目标类型。
     * @param <T>        返回值的泛型类型。
     * @return 类型为 T 的单个结果，如果未查询到结果则返回 {@code null}。
     */
    <T> T selectOne(String sql, Class<T> returnType);

    /**
     * 使用预编译的方式执行指定的 SQL 查询，返回映射为指定类型的单个结果。
     *
     * @param sql             要执行的 SQL 查询语句。
     * @param returnType      结果映射的目标类型。
     * @param parameterBinder 参数绑定器，用于绑定查询中的参数。
     * @param <T>             返回值的泛型类型。
     * @return 类型为 T 的单个结果，如果未查询到结果则返回 {@code null}。
     */
    <T> T selectOne(String sql, Class<T> returnType, ParameterBinder parameterBinder);

    /**
     * 执行指定数据源上的 SQL 查询，返回映射为指定类型的单个结果。
     *
     * @param dataSourceName 数据源名称，未指定默认按照优先级匹配最佳数据源
     * @param sql            要执行的 SQL 查询语句。
     * @param returnType     结果映射的目标类型。
     * @param <T>            返回值的泛型类型。
     * @return 类型为 T 的单个结果，如果未查询到结果则返回 {@code null}。
     */
    <T> T selectOne(String dataSourceName, String sql, Class<T> returnType);

    /**
     * 使用预编译的方式在指定数据源上执行 SQL 查询，返回映射为指定类型的单个结果。
     * <p>
     * 使用示例：<pre>{@code
     *         ParameterBinder parameterBinder = new ParameterBinder();
     *         String value = SqlUtils.registerValueWithKey(parameterBinder, 1);
     *         String sql = "SELECT x.user_id FROM users x where x.user_id = %s";
     *         sqlContext.selectOne("dataSource", String.format(sql, value), Integer.class, parameterBinder);
     * }</pre>
     *
     * @param dataSourceName  数据源名称，未指定默认按照优先级匹配最佳数据源
     * @param sql             要执行的 SQL 查询语句。
     * @param returnType      结果映射的目标类型。
     * @param parameterBinder 参数绑定器，用于绑定查询中的参数。
     * @param <T>             返回值的泛型类型。
     * @return 类型为 T 的单个结果，如果未查询到结果则返回 {@code null}。
     */
    <T> T selectOne(String dataSourceName, String sql, Class<T> returnType, ParameterBinder parameterBinder);

    /**
     * 执行指定的 SQL 查询，返回映射为指定类型的结果列表。
     *
     * @param sql        要执行的 SQL 查询语句。
     * @param returnType 结果映射的目标类型。
     * @param <T>        返回值的泛型类型。
     * @return 类型为 T 的结果列表，如果未查询到结果则返回空列表。
     */
    <T> List<T> selectList(String sql, Class<T> returnType);

    /**
     * 使用预编译的方式执行指定的 SQL 查询，返回映射为指定类型的结果列表。
     *
     * @param sql             要执行的 SQL 查询语句。
     * @param returnType      结果映射的目标类型。
     * @param parameterBinder 参数绑定器，用于绑定查询中的参数。
     * @param <T>             返回值的泛型类型。
     * @return 类型为 T 的结果列表，如果未查询到结果则返回空列表。
     */
    <T> List<T> selectList(String sql, Class<T> returnType, ParameterBinder parameterBinder);

    /**
     * 执行指定数据源上的 SQL 查询，返回映射为指定类型的结果列表。
     *
     * @param dataSourceName 数据源名称，未指定默认按照优先级匹配最佳数据源
     * @param sql            要执行的 SQL 查询语句。
     * @param returnType     结果映射的目标类型。
     * @param <T>            返回值的泛型类型。
     * @return 类型为 T 的结果列表，如果未查询到结果则返回空列表。
     */
    <T> List<T> selectList(String dataSourceName, String sql, Class<T> returnType);

    /**
     * 使用预编译的方式在指定数据源上执行 SQL 查询，返回映射为指定类型的结果列表。
     * <p>
     * 使用示例：<pre>{@code
     *         ParameterBinder parameterBinder = new ParameterBinder();
     *         String value = SqlUtils.registerValueWithKey(parameterBinder, 1);
     *         String sql = "SELECT x.user_id FROM users x where x.user_id >= %s";
     *         sqlContext.selectList("dataSource", String.format(sql, value), Integer.class, parameterBinder);
     * }</pre>
     *
     * @param dataSourceName  数据源名称，未指定默认按照优先级匹配最佳数据源
     * @param sql             要执行的 SQL 查询语句。
     * @param returnType      结果映射的目标类型。
     * @param parameterBinder 参数绑定器，用于绑定查询中的参数。
     * @param <T>             返回值的泛型类型。
     * @return 类型为 T 的结果列表，如果未查询到结果则返回空列表。
     */
    <T> List<T> selectList(String dataSourceName, String sql, Class<T> returnType, ParameterBinder parameterBinder);

    /**
     * 插入一个实体到数据库，选择性插入非空字段。
     *
     * @param entity 要插入的实体对象，不能为空。
     * @param <T>    实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     * @throws IllegalArgumentException 如果提供的实体为 null。
     */
    <T> int insertSelective(T entity);

    /**
     * 插入一个实体到数据库，选择性插入非空字段，并强制插入指定字段。
     *
     * @param entity       要插入的实体对象，不能为空。
     * @param forcedFields 强制插入的字段集合，可以为 null 或空集合。
     * @param <T>          实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insertSelective(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 插入一个实体到数据库，插入所有字段。
     *
     * @param entity 要插入的实体对象，不能为空。
     * @param <T>    实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insert(T entity);

    /**
     * 批量插入一组实体到数据库。
     * <p>
     * 使用批处理模式插入多个记录，更加稳定但执行时间相对较长。
     *
     * @param entities 要插入的实体集合，不能为空。
     * @param <T>      实体的类型，一般为数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insertBatch(Collection<T> entities);

    /**
     * 通过追加模式插入多条记录到数据库。
     * <p>
     * 追加模式更高效，但可能有局限性；<br>
     * 比如 SQL 语句长度超出数据库限制。
     *
     * @param entities 要插入的实体集合，不能为空。
     * @param <T>      实体的类型，一般为数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insertMultiple(Collection<T> entities);

    /**
     * 根据主键值删除数据库中的单条记录。
     *
     * @param <T>         实体类的类型。
     * @param entityClass 实体类，表示需要操作的表。
     * @param pkValue     主键值，指定要删除的记录。
     * @return 返回删除的记录条数
     */
    <T> int deleteByPrimaryKey(Class<T> entityClass, Object pkValue);

    /**
     * 根据主键值集合删除数据库中的多条记录。
     *
     * @param <T>         实体类的类型。
     * @param entityClass 实体类，表示需要操作的表。
     * @param pkValues    主键值集合，指定要删除的多条记录。
     * @return 返回删除的记录条数
     */
    <T> int deleteByPrimaryKey(Class<T> entityClass, Collection<?> pkValues);

    /**
     * 根据指定的条件删除数据库中的记录。
     *
     * @param <T>         实体类的类型。
     * @param entityClass 实体类，表示需要操作的表。
     * @param condition   条件构造器，使用 {@link Consumer} 定义删除条件。
     *                    如果传入 null，则删除整张表的所有数据。
     * @return 返回删除的记录条数。
     */
    <T> int delete(Class<T> entityClass, Consumer<GenericWhereCondition> condition);

    /**
     * 根据主键更新整个实体对象的所有字段。
     *
     * @param <T>    实体类的类型。
     * @param entity 包含主键和其他字段的实体对象，所有字段将被更新。
     * @return 返回更新的记录条数
     */
    <T> int updateByPrimaryKey(T entity);

    /**
     * 根据主键更新实体对象中非空字段。
     *
     * @param <T>    实体类的类型。
     * @param entity 包含主键的实体对象，仅更新非空字段。
     * @return 返回更新的记录条数
     */
    <T> int updateSelectiveByPrimaryKey(T entity);

    /**
     * 根据主键更新实体对象中非空字段，并强制更新指定的字段。
     *
     * @param <T>          实体类的类型。
     * @param entity       包含主键的实体对象，仅更新非空字段。
     * @param forcedFields 需要强制更新的字段，即使字段值为空也会更新。
     * @return 返回更新的记录条数
     */
    <T> int updateSelectiveByPrimaryKey(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 根据指定条件更新实体对象的所有字段。
     *
     * @param <T>       实体类的类型。
     * @param entity    实体对象，表示需要更新的数据。
     * @param condition 条件构造器，使用 {@link Consumer} 定义更新条件，为null更新所有。
     * @return 返回更新的记录条数。
     */
    <T> int update(T entity, Consumer<GenericWhereCondition> condition);

    /**
     * 根据指定条件更新实体对象的非空字段。
     *
     * @param <T>       实体类的类型。
     * @param entity    实体对象，仅更新非空字段。
     * @param condition 条件构造器，使用 {@link Consumer} 定义更新条件，为null更新所有。
     * @return 返回更新的记录条数。
     */
    <T> int updateSelective(T entity, Consumer<GenericWhereCondition> condition);

    /**
     * 根据指定条件更新实体对象的非空字段，并强制更新指定的字段。
     *
     * @param <T>          实体类的类型。
     * @param entity       实体对象，仅更新非空字段。
     * @param forcedFields 需要强制更新的字段，即使字段值为空也会更新。
     * @param condition    条件构造器，使用 {@link Consumer} 定义更新条件，为null更新所有。
     * @return 返回更新的记录条数。
     */
    <T> int updateSelective(T entity, Collection<Fn<T, ?>> forcedFields, Consumer<GenericWhereCondition> condition);

    /**
     * 插入或更新实体对象的所有字段。
     * <p>
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则更新所有字段。
     *
     * @param <T>    实体类的类型。
     * @param entity 要插入或更新的实体对象。
     * @return 返回新增或更新的总记录条数，通常为 1 或 2。
     */
    <T> int upsert(T entity);

    /**
     * 插入或更新实体（仅更新非空字段）。
     * <p>
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则仅更新非空字段。
     *
     * @param <T>    实体类的类型。
     * @param entity 要插入或更新的实体对象。
     * @return 返回新增或更新的总记录条数，通常为 1 或 2。
     */
    <T> int upsertSelective(T entity);

    /**
     * 插入或更新实体（仅更新非空字段），并强制更新指定字段。
     * <p>
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则仅更新非空字段，并强制更新指定字段。
     *
     * @param <T>          实体类的类型。
     * @param entity       要插入或更新的实体对象。
     * @param forcedFields 需要强制更新的字段，即使字段值为空也会更新。
     * @return 返回新增或更新的总记录条数，通常为 1 或 2。
     */
    <T> int upsertSelective(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 批量插入或更新实体对象的所有字段。
     * <p>
     * 对于每个实体：
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则更新所有字段。
     *
     * @param <T>      实体类的类型。
     * @param entities 包含需要插入或更新的实体对象集合。
     * @return 返回新增或更新的总记录条数。
     */
    <T> int upsertMultiple(Collection<T> entities);

    /**
     * 执行指定的 SQL 语句。
     *
     * @param sql 要执行的 SQL 语句字符串。例如，查询语句（如 "select * from users limit 10"）或 DDL 语句（如 CREATE TABLE）。
     * @return 执行 SQL 后的结果对象，具体类型取决于 SQL 类型和数据库实现。例如，查询返回结果集，DDL 操作可能返回成功标志或空对象。
     */
    default Object execute(String sql) {
        return execute(sql, new ParameterBinder());
    }

    /**
     * 执行带参数绑定的 SQL 语句。
     *
     * @param sql             要执行的 SQL 语句字符串，其中可以使用占位符。
     * @param parameterBinder 参数绑定器
     * @return 执行 SQL 后的结果对象，具体类型取决于 SQL 类型和数据库实现。例如，查询返回结果集，DML 操作可能返回受影响的行数。
     * @see SqlUtils#registerValueWithKey(ParameterBinder, Object) 管理参数的注册和绑定
     */
    default Object execute(String sql, ParameterBinder parameterBinder) {
        return execute(null, sql, parameterBinder);
    }

    /**
     * 执行指定数据源的带参数绑定的 SQL 语句。
     *
     * @param dataSourceName  数据源名称，用于指定执行 SQL 的数据库连接。
     * @param sql             要执行的 SQL 语句字符串，其中可以使用占位符（如通过  注册的参数键）。
     *                        示例： "SELECT * FROM users WHERE id = {@link SqlUtils#registerValueWithKey(ParameterBinder, Object)}" 或 "CREATE TABLE users (id INT)"。
     * @param parameterBinder 参数绑定器，负责管理 SQL 语句中需要替换的实际参数值。确保 SQL 执行安全并避免 SQL 注入。
     * @return 执行 SQL 后的结果对象，具体类型取决于 SQL 类型和数据库实现。例如：<br>
     * - 对于 SELECT 语句，返回查询结果集（通常是 List<Map<String, Object>> ）。<br>
     * - 对于 INSERT、UPDATE、DELETE 语句，返回受影响的行数（Integer）。<br>
     * - 对于 CREATE、ALTER、DROP 等 DDL 语句，返回成功标志（通常为 Integer）。
     * @see SqlUtils#registerValueWithKey(ParameterBinder, Object) 用于管理参数的注册和绑定。
     */
    Object execute(String dataSourceName, String sql, ParameterBinder parameterBinder);

    /**
     * 使用默认数据源获取目录、模式和名称模式下所有匹配的表元数据。
     *
     * @see this#getAllTableMetaData(String, String, String, String, String[])
     */
    default List<TableMetaData> getAllTableMetaData(String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes) {
        return getAllTableMetaData(null, catalog, schemaPattern, tableNamePattern, tableTypes);
    }

    /**
     * 获取指定数据源、目录、模式和名称模式下所有匹配的表元数据。
     *
     * @param dataSourceName   数据源名称，用于指定执行查询的数据库连接。如果系统支持多个数据源，则通过此参数选择目标数据源。
     * @param catalog          目录名称，可为 null（如果数据库不支持目录或无需指定，在MySQL中等价于database）。
     * @param schemaPattern    模式名称或模式模式，可为 null（如果数据库不支持模式或无需指定，比如在MySQL中通常为null）。
     * @param tableNamePattern 表名称模式，支持通配符（如 "%" 匹配所有表，"user%" 匹配以 "user" 开头的表）。
     *                         不能为空或空字符串。
     * @param tableTypes       表类型的数组（如 "TABLE", "VIEW", "SYSTEM TABLE"），可为 null（默认只返回 "TABLE" 类型）。
     * @return 匹配的表元数据列表，每个元素是 {@link TableMetaData} 对象，包含表的详细信息（如名称、类型、备注等）。
     * 如果没有匹配的表，返回空列表（非 null）。
     * @throws IllegalStateException 如果数据源名称无效、数据库连接失败或参数无效。
     * @see DatabaseMetaData#getTables(String, String, String, String[]) 获取表的元数据。
     */
    List<TableMetaData> getAllTableMetaData(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes);

    /**
     * 使用默认数据源获取目录、模式、表名称模式和列名称模式下所有匹配的列元数据
     *
     * @see this#getAllColumnMetaData(String, String, String, String, String)
     */
    default List<ColumnMetaData> getAllColumnMetaData(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        return getAllColumnMetaData(null, catalog, schemaPattern, tableNamePattern, columnNamePattern);
    }

    /**
     * 获取指定数据源、目录、模式、表名称模式和列名称模式下所有匹配的列元数据。
     *
     * @param dataSourceName    数据源名称，用于指定执行查询的数据库连接。
     * @param catalog           目录名称，可为 null（如果数据库不支持目录或无需指定）。
     * @param schemaPattern     模式名称或模式模式，可为 null（如果数据库不支持模式或无需指定）。
     * @param tableNamePattern  表名称模式，支持通配符（如 "%" 匹配所有表，"user%" 匹配以 "user" 开头的表）。
     *                          不能为空或空字符串。
     * @param columnNamePattern 列名称模式，支持通配符（如 "%" 匹配所有列，"id%" 匹配以 "id" 开头的列）。
     *                          可为 null，表示匹配所有列。
     * @return 匹配的列元数据列表，每个元素是 {@link ColumnMetaData} 对象，包含列的详细信息（如名称、类型、注释等）。
     * 如果没有匹配的列，返回空列表（非 null）。
     * @throws IllegalStateException 如果数据源名称无效、数据库连接失败或参数无效。
     * @see DatabaseMetaData#getColumns(String, String, String, String) 获取列的元数据。
     */
    List<ColumnMetaData> getAllColumnMetaData(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern);

}
