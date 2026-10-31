/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database;

import com.dynamic.sql.core.dml.insert.EntitiesInserter;
import com.dynamic.sql.enums.GenerationType;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.GeneratedStrategy;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.ConverterUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class RootExecutor {
    private RootExecutor() {
    }

    private static final Logger log = LoggerFactory.getLogger(RootExecutor.class);

    /**
     * 使用通用 JDBC 规则查询；其他方言不会自动启用 MySQL 的时间读取规则。
     */
    public static List<Map<String, Object>> executeQuery(Connection connection, PreparedSql preparedSql) {
        return executeQuery(connection, preparedSql, false);
    }

    /**
     * 执行查询并保留列标签顺序，必要时保存 MySQL 时间列的精确信息。
     *
     * @param connection                  当前数据库连接，由调用方管理释放
     * @param preparedSql                 已生成占位符及参数的 SQL
     * @param preserveMysqlTemporalValues 是否额外保留 MySQL 时间文本，用于 Java 时间类型映射
     * @return 每行以列标签为键的查询结果
     */
    public static List<Map<String, Object>> executeQuery(Connection connection, PreparedSql preparedSql,
                                                        boolean preserveMysqlTemporalValues) {
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(preparedSql.getSql());
            List<Object> params = preparedSql.getParams();
            for (int i = 1; i <= params.size(); i++) {
                preparedStatement.setObject(i, params.get(i - 1));
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            HashSet<String> detectedColumnNameSet = new HashSet<>();
            while (resultSet.next()) {
                JdbcResultRow objectMap = new JdbcResultRow();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    if (log.isDebugEnabled() && objectMap.containsKey(columnName)) {
                        detectedColumnNameSet.add(columnName);
                    }
                    //重复列标签采用后值覆盖前值，不能残留前一个时间列的补充信息。
                    objectMap.clearTemporal(columnName);
                    Object columnValue;
                    int columnType = metaData.getColumnType(i);
                    if (columnType == Types.TINYINT || (columnType == Types.BIT && metaData.getPrecision(i) <= 1)) {
                        //TINYINT(1) 可能被驱动描述为单比特类型；按整数读取，保留实际的非 0/1 数值。
                        int number = resultSet.getInt(i);
                        //getInt 遇到 SQL NULL 也返回 0，必须立即通过 wasNull 区分空值与数值零。
                        columnValue = resultSet.wasNull() ? null : number;
                    } else if (columnType == Types.BIT) {
                        //多位 BIT 是位串，直接读取字节，避免 BIT(64) 高位被截断或触发 Integer 越界。
                        columnValue = resultSet.getBytes(i);
                    } else if (preserveMysqlTemporalValues && columnType == Types.TIME) {
                        String time = resultSet.getString(i);
                        //java.sql.Time 无法完整保留 TIME(6) 的微秒，额外保存文本供 LocalTime 映射使用。
                        objectMap.putTemporal(columnName, time);
                        //MySQL TIME 还可以表示负数或超过 24 小时的时长，不能强制读取为 java.sql.Time。
                        if (time != null && (time.startsWith("-") || Integer.parseInt(time.substring(0,time.indexOf(':'))) >= 24)) {
                            columnValue = time;
                        } else {
                            columnValue = resultSet.getObject(i);
                        }
                    } else {
                        columnValue = resultSet.getObject(i);
                        if (preserveMysqlTemporalValues && columnType == Types.TIMESTAMP) {
                            //TIMESTAMP 文本使用数据库会话时区；DATETIME 文本保持原值。
                            //映射 LocalDateTime 时使用此文本，避免 Timestamp.toLocalDateTime 受 JVM 时区影响。
                            objectMap.putTemporal(columnName, resultSet.getString(i));
                        }
                    }
                    objectMap.put(columnName, columnValue);
                }
                arrayList.add(objectMap);
            }
            if (log.isDebugEnabled()) {
                detectedColumnNameSet.forEach(columnName -> log.debug("Duplicate column name detected: {}", columnName));
            }
        } catch (SQLException e) {
            throw new DynamicSqlException(e);
        } finally {
            SqlUtils.close(resultSet, preparedStatement);
        }
        return arrayList;
    }

    public static int executeInsert(Connection connection, PreparedSql preparedSql) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int rowsAffected;
        ColumnMeta columnMeta = extractAutoIncrementColumnMeta();
        try {
            if (columnMeta == null) {
                preparedStatement = connection.prepareStatement(preparedSql.getSql());
            } else {
                preparedStatement = connection.prepareStatement(preparedSql.getSql(), Statement.RETURN_GENERATED_KEYS);
            }
            List<Object> params = preparedSql.getParams();
            for (int i = 1; i <= params.size(); i++) {
                preparedStatement.setObject(i, params.get(i - 1));
            }
            rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected <= 0 || columnMeta == null) {
                return rowsAffected;
            }
            // 获取自增键值
            resultSet = preparedStatement.getGeneratedKeys();
            assignValueToPrimaryKey(resultSet, columnMeta);
        } catch (SQLException e) {
            throw new DynamicSqlException(e);
        } finally {
            SqlUtils.close(resultSet, preparedStatement);
        }
        return rowsAffected;
    }

    public static int executeUpdate(Connection connection, PreparedSql preparedSql) {
        PreparedStatement preparedStatement = null;
        int rowsAffected;
        try {
            preparedStatement = connection.prepareStatement(preparedSql.getSql());
            List<Object> params = preparedSql.getParams();
            for (int i = 1; i <= params.size(); i++) {
                preparedStatement.setObject(i, params.get(i - 1));
            }
            rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected <= 0) {
                return rowsAffected;
            }
        } catch (SQLException e) {
            throw new DynamicSqlException(e);
        } finally {
            SqlUtils.close(null, preparedStatement);
        }
        return rowsAffected;
    }

    public static int executeInsertBatch(Connection connection, PreparedSql preparedSql) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int rowsAffected;
        ColumnMeta columnMeta = extractAutoIncrementColumnMeta();
        try {
            if (columnMeta == null) {
                preparedStatement = connection.prepareStatement(preparedSql.getSql());
            } else {
                preparedStatement = connection.prepareStatement(preparedSql.getSql(), Statement.RETURN_GENERATED_KEYS);
            }
            List<List<Object>> batchParams = preparedSql.getBatchParams();
            for (List<Object> batchParam : batchParams) {
                for (int i = 1; i <= batchParam.size(); i++) {
                    preparedStatement.setObject(i, batchParam.get(i - 1));
                }
                preparedStatement.addBatch();
            }
            rowsAffected = preparedStatement.executeBatch().length;
            if (rowsAffected <= 0 || columnMeta == null) {
                return rowsAffected;
            }
            // 获取自增键值
            resultSet = preparedStatement.getGeneratedKeys();
            assignValueToPrimaryKey(resultSet, columnMeta);
        } catch (SQLException e) {
            throw new DynamicSqlException(e);
        } finally {
            SqlUtils.close(resultSet, preparedStatement);
        }
        return rowsAffected;
    }


    private static ColumnMeta extractAutoIncrementColumnMeta() {
        Collection<Object> localEntities = EntitiesInserter.getLocalEntities();
        if (CollectionUtils.isEmpty(localEntities)) {
            return null;
        }
        Object next = localEntities.iterator().next();
        TableMeta tableMeta = TableProvider.getTableMeta(next.getClass());
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        return columnMetas.stream().filter(columnMeta -> {
            GeneratedStrategy generatedStrategy = columnMeta.getGeneratedStrategy();
            return generatedStrategy != null && (GenerationType.AUTO.equals(generatedStrategy.getStrategy()));
        }).findFirst().orElse(null);
    }

    private static void assignValueToPrimaryKey(ResultSet resultSet, ColumnMeta columnMeta) throws SQLException {
        for (Object obj : EntitiesInserter.getLocalEntities()) {
            if (!resultSet.next()) {
                return;
            }
            Long generatedKey = resultSet.getLong(1);
            // 设置自增ID值到实体对应字段
            Object o = ConverterUtils.convertToEntityAttribute(columnMeta, columnMeta.getField().getType(), generatedKey);
            ReflectUtils.setFieldValue(obj, columnMeta.getField(), o);
        }
    }

}