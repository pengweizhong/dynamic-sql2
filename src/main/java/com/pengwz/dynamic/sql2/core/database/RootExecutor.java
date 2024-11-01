package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.core.dml.insert.impl.EntitiesInserter;
import com.pengwz.dynamic.sql2.enums.GenerationType;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.GeneratedStrategy;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class RootExecutor {
    private RootExecutor() {
    }

    private static final Logger log = LoggerFactory.getLogger(RootExecutor.class);

    public static List<Map<String, Object>> executeQuery(Connection connection, PreparedSql preparedSql) {
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
            while (resultSet.next()) {
                LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    if (objectMap.containsKey(columnName)) {
                        log.error("Duplicate column name detected: {}", columnName);
                        throw new IllegalArgumentException("Duplicate column name: " + columnName);
                    }
                    Object columnValue = resultSet.getObject(i);
                    objectMap.put(columnName, columnValue);
                }
                arrayList.add(objectMap);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
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
            throw new IllegalStateException(e);
        } finally {
            SqlUtils.close(resultSet, preparedStatement);
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
            throw new IllegalStateException(e);
        } finally {
            SqlUtils.close(resultSet, preparedStatement);
        }
        return rowsAffected;
    }

    public static int executeInsertMultiple(Connection connection, PreparedSql preparedSql) {
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
            List<Object> multipleParams = preparedSql.getParams();
            for (int i = 1; i <= multipleParams.size(); i++) {
                preparedStatement.setObject(i, multipleParams.get(i - 1));
            }
            rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected <= 0 || columnMeta == null) {
                return rowsAffected;
            }
            // 获取自增键值
            resultSet = preparedStatement.getGeneratedKeys();
            assignValueToPrimaryKey(resultSet, columnMeta);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            SqlUtils.close(resultSet, preparedStatement);
        }
        return rowsAffected;
    }

    private static ColumnMeta extractAutoIncrementColumnMeta() {
        Collection<Object> localEntities = EntitiesInserter.getLocalEntities();
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
            resultSet.next();
            Long generatedKey = resultSet.getLong(1);
            // 设置自增ID值到实体对应字段
            Object o = ConverterUtils.convertToEntityAttribute(columnMeta.getField().getType(), generatedKey);
            ReflectUtils.setFieldValue(obj, columnMeta.getField(), o);
        }
    }

}