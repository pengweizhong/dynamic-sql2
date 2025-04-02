package com.dynamic.sql.core.database.impl;

import com.dynamic.sql.core.database.AbstractSqlExecutor;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.database.RootExecutor;
import com.dynamic.sql.model.TableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MysqlSqlExecutor extends AbstractSqlExecutor {
    private static final Logger log = LoggerFactory.getLogger(MysqlSqlExecutor.class);

    public MysqlSqlExecutor(Connection connection, PreparedSql preparedSql) {
        super(connection, preparedSql);
    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return RootExecutor.executeQuery(connection, preparedSql);
    }

    @Override
    public int insertSelective() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int insert() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int insertBatch() {
        return RootExecutor.executeInsertBatch(connection, preparedSql);
    }

    @Override
    public int insertMultiple() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int deleteByPrimaryKey() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int delete() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int updateByPrimaryKey() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int updateSelectiveByPrimaryKey() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int update() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int updateSelective() {
        return RootExecutor.executeUpdate(connection, preparedSql);
    }

    @Override
    public int upsert() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int upsertSelective() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int upsertMultiple() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public List<TableMetaData> getAllTableMetaData(String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes) {
        List<TableMetaData> metaDataList = new ArrayList<>();
        try (ResultSet rs = connection.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, tableTypes)) {
            boolean exists = rs != null && rs.next();
            if (exists) {
                TableMetaData tableMetaData = new TableMetaData();
                tableMetaData.setTableCatalog(rs.getString("TABLE_CAT"));
                tableMetaData.setTableSchema(rs.getString("TABLE_SCHEM")); // 模式
                tableMetaData.setTableName(rs.getString("TABLE_NAME")); // 表名
                tableMetaData.setTableType(rs.getString("TABLE_TYPE")); // 表类型
                tableMetaData.setRemarks(rs.getString("REMARKS")); // 备注
                // 扩展：获取更多元数据（如创建时间、列信息等）
                tableMetaData.setTypeCat(rs.getString("TYPE_CAT"));
                tableMetaData.setTypeSchem(rs.getString("TYPE_SCHEM"));
                tableMetaData.setTypeName(rs.getString("TYPE_NAME"));
                tableMetaData.setSelfReferencingColName(rs.getString("SELF_REFERENCING_COL_NAME"));
                tableMetaData.setRefGeneration(rs.getString("REF_GENERATION"));
                metaDataList.add(tableMetaData);
            }
            return metaDataList;
        } catch (SQLException e) {
            log.error("Failed to check table existence for table: {}", preparedSql.getSql(), e);
            throw new IllegalStateException(e);
        }
    }
}
