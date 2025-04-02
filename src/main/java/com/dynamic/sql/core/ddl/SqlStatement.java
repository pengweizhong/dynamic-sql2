package com.dynamic.sql.core.ddl;

import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SqlStatement {
    //数据源名称
    private String dataSourceName;
    //需要执行的任意SQL
    private String sql;
    //SQL对应的待编译的参数
    private ParameterBinder parameterBinder;

    public SqlStatement(String dataSourceName, String sql, ParameterBinder parameterBinder) {
        if (StringUtils.isEmpty(sql)) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty");
        }
        this.dataSourceName = dataSourceName;
        this.sql = sql.trim();
        this.parameterBinder = parameterBinder;
    }

    public String getSql() {
        return sql;
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    public String getDataSourceName() {
        return dataSourceName == null ? DataSourceProvider.getDefaultDataSourceName() : dataSourceName;
    }

    public Object execute() {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(getDataSourceName(), new StringBuilder(sql), getParameterBinder());
        String sqlType = parseSqlType(sql);
        if (sqlType == null) {
            throw new IllegalStateException("Unable to determine SQL type for statement: " + sql);
        }
        try {
            return executeSqlByType(sqlType, sqlStatementWrapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL: " + sql, e);
        }
    }

    /**
     * 解析 SQL 语句的类型
     */
    private String parseSqlType(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return null;
        }
        int firstSpace = sql.indexOf(" ");
        String keyword = (firstSpace > 0) ? sql.substring(0, firstSpace) : sql;
        // 映射关键字到类型
        Map<String, String> sqlTypeMap = new HashMap<>();
        sqlTypeMap.put("SELECT", DMLType.SELECT.name());
        sqlTypeMap.put("INSERT", DMLType.INSERT.name());
        sqlTypeMap.put("UPDATE", DMLType.UPDATE.name());
        sqlTypeMap.put("DELETE", DMLType.DELETE.name());
        sqlTypeMap.put("CREATE", DDLType.CREATE.name());
        sqlTypeMap.put("ALTER", DDLType.ALTER.name());
        sqlTypeMap.put("DROP", DDLType.DROP.name());
        sqlTypeMap.put("TRUNCATE", DDLType.TRUNCATE.name());
        return sqlTypeMap.getOrDefault(keyword.toUpperCase(), DMLType.UPDATE.name()); // 默认使用 UPDATE
    }

    /**
     * 根据 SQL 类型执行语句
     */
    private Object executeSqlByType(String sqlType, SqlStatementWrapper sqlStatementWrapper) {
        // 定义执行器映射
        Map<String, BiFunction<SqlExecuteType, SqlStatementWrapper, Object>> executorMap = new HashMap<>();
        executorMap.put(DMLType.SELECT.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::executeQuery));
        executorMap.put(DMLType.INSERT.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::insert));
        executorMap.put(DMLType.DELETE.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::delete));
        executorMap.put(DDLType.CREATE.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::update));
        executorMap.put(DDLType.ALTER.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::update));
        executorMap.put(DDLType.DROP.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::update));
        executorMap.put(DDLType.TRUNCATE.name(), (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::update));

        // 获取执行器
        SqlExecuteType executeType = getExecuteType(sqlType);
        BiFunction<SqlExecuteType, SqlStatementWrapper, Object> executor = executorMap.getOrDefault(sqlType,
                //默认更新
                (type, wrapper) -> SqlExecutionFactory.executorSql(type, wrapper, SqlExecutor::update));
        return executor.apply(executeType, sqlStatementWrapper);
    }

    /**
     * 根据类型字符串获取对应的枚举类型
     */
    private SqlExecuteType getExecuteType(String typeName) {
        try {
            return DMLType.valueOf(typeName); // 尝试解析为 DML 类型
        } catch (IllegalArgumentException e1) {
            try {
                return DDLType.valueOf(typeName); // 尝试解析为 DDL 类型
            } catch (IllegalArgumentException e2) {
                throw new IllegalStateException("Unknown SQL type: " + typeName);
            }
        }
    }

}
