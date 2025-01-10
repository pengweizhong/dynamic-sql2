package com.dynamic.sql.core.database;

import com.dynamic.sql.core.dml.insert.EntitiesInserter;
import com.dynamic.sql.enums.GenerationType;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.GeneratedStrategy;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
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
            HashSet<String> detectedColumnNameSet = new HashSet<>();
            while (resultSet.next()) {
                LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    if (objectMap.containsKey(columnName)) {
                        detectedColumnNameSet.add(columnName);
//                        throw new IllegalArgumentException("Duplicate column name: " + columnName);
                    }
                    Object columnValue = resultSet.getObject(i);
                    objectMap.put(columnName, columnValue);
                }
                arrayList.add(objectMap);
            }
            detectedColumnNameSet.forEach(columnName -> log.warn("Duplicate column name detected: {}", columnName));
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
            throw new IllegalStateException(e);
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