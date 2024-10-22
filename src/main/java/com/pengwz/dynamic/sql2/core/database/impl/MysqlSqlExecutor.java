package com.pengwz.dynamic.sql2.core.database.impl;

import com.pengwz.dynamic.sql2.core.database.AbstractSqlExecutor;
import com.pengwz.dynamic.sql2.core.database.PreparedObject;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MysqlSqlExecutor extends AbstractSqlExecutor {
    private static final Logger log = LoggerFactory.getLogger(MysqlSqlExecutor.class);

    public MysqlSqlExecutor(Connection connection, SqlStatementWrapper sqlStatementWrapper) {
        super(connection, sqlStatementWrapper);
    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        PreparedObject preparedObject = SqlUtils.parsePreparedObject(rawSql, parameterBinder);
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(preparedObject.getSql());
            List<Object> params = preparedObject.getParams();
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
}
