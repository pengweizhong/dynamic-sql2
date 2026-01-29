/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.ddl;

import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.dml.select.FetchResultImpl;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SqlStatement {
    //数据源名称
    private String dataSourceName;
    //需要执行的任意SQL
    private String sql;
    //SQL对应的待编译的参数
    private ParameterBinder parameterBinder;

    public SqlStatement(String dataSourceName, String sql, ParameterBinder parameterBinder) {
        if (StringUtils.isEmpty(sql)) {
            throw new DynamicSqlException("SQL statement cannot be null or empty");
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

    public <T> T execute(Class<T> returnType) {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(getDataSourceName(), new StringBuilder(sql), getParameterBinder());
        String sqlType = parseSqlType(sql);
        if (sqlType == null) {
            throw new DynamicSqlException("Unable to determine SQL type for statement: " + sql);
        }
        try {
            return executeSqlByType(sqlType, sqlStatementWrapper, returnType);
        } catch (Exception e) {
            throw new DynamicSqlException("Failed to execute SQL: " + sql, e);
        }
    }

    public <T, L extends List<T>> L execute(Class<T> returnType, Supplier<L> listSupplier) {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(getDataSourceName(), new StringBuilder(sql), getParameterBinder());
        String sqlType = parseSqlType(sql);
        if (sqlType == null) {
            throw new DynamicSqlException("Unable to determine SQL type for statement: " + sql);
        }
        if (!Objects.equals(sqlType, DMLType.SELECT.name())) {
            throw new DynamicSqlException("Only accepting SQL queries: " + sql);
        }
        try {
            List<Map<String, Object>> maps = SqlExecutionFactory.executorSql(DMLType.SELECT, sqlStatementWrapper, SqlExecutor::executeQuery);
            FetchResultImpl<T> tFetchResult = new FetchResultImpl<>(returnType, maps, null);
            return tFetchResult.toList(listSupplier);
        } catch (Exception e) {
            throw new DynamicSqlException("Failed to execute SQL: " + sql, e);
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
    private <T> T executeSqlByType(String sqlType, SqlStatementWrapper sqlStatementWrapper, Class<T> returnType) {
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
        Object apply = executor.apply(executeType, sqlStatementWrapper);
        if (apply.getClass().isAssignableFrom(returnType) || Objects.equals(returnType, Object.class)) {
            return (T) apply;
        }
        if (Objects.equals(sqlType, DMLType.SELECT.name())) {
            FetchResultImpl<T> tFetchResult = new FetchResultImpl<>(returnType, (List<Map<String, Object>>) apply, null);
            return tFetchResult.toOne();
        }
        return (T) apply;
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
                throw new DynamicSqlException("Unknown SQL type: " + typeName);
            }
        }
    }

}
