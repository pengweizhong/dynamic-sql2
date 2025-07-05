/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database.impl;

import com.dynamic.sql.core.database.AbstractSqlExecutor;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.database.RootExecutor;
import com.dynamic.sql.model.ColumnMetaData;
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
            if (rs != null && rs.next()) {
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
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<ColumnMetaData> getAllColumnMetaData(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        try (ResultSet rs = connection.getMetaData().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)) {
            List<ColumnMetaData> metaDataList = new ArrayList<>();
            while (rs != null && rs.next()) {
                ColumnMetaData columnMeta = new ColumnMetaData();
                columnMeta.setTableCatalog(rs.getString("TABLE_CAT"));
                columnMeta.setTableSchema(rs.getString("TABLE_SCHEM"));
                columnMeta.setTableName(rs.getString("TABLE_NAME"));
                columnMeta.setColumnName(rs.getString("COLUMN_NAME"));
                columnMeta.setDataType(rs.getInt("DATA_TYPE"));
                columnMeta.setTypeName(rs.getString("TYPE_NAME"));
                columnMeta.setColumnSize(rs.getInt("COLUMN_SIZE"));
                columnMeta.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                columnMeta.setNumPrecRadix(rs.getInt("NUM_PREC_RADIX"));
                columnMeta.setNullable(rs.getInt("NULLABLE"));
                columnMeta.setRemarks(rs.getString("REMARKS"));
                columnMeta.setColumnDef(rs.getString("COLUMN_DEF"));
                columnMeta.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
                columnMeta.setIsNullable(rs.getString("IS_NULLABLE"));
                // MySQL 通常为空的字段
                columnMeta.setScopeCatalog(rs.getString("SCOPE_CATALOG"));
                columnMeta.setScopeSchema(rs.getString("SCOPE_SCHEMA"));
                columnMeta.setScopeTable(rs.getString("SCOPE_TABLE"));
                columnMeta.setSourceDataType(rs.getShort("SOURCE_DATA_TYPE"));
                metaDataList.add(columnMeta);
            }
            return metaDataList;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
