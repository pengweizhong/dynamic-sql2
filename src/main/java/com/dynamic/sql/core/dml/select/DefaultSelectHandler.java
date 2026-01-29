/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.anno.Table;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultSelectHandler implements SelectHandler {
    @Override
    public List<Map<String, Object>> executeQuery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T selectByPrimaryKey(Class<T> entityClass, Object pkValue) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            return null;
        }
        ColumnMeta columnPrimaryKey = tableMeta.getColumnPrimaryKey();
        if (columnPrimaryKey == null) {
            return null;
        }
        Column column = new Column(tableMeta.getTableAlias(), columnPrimaryKey.getColumnName());
        return new Select().loadColumReference().allColumn().from(entityClass).where(whereCondition ->
                whereCondition.andEqualTo(column, pkValue)).fetch().toOne();
    }

    @Override
    public <T> List<T> selectByPrimaryKey(Class<T> entityClass, Collection<?> pkValues) {
        TableMeta tableMeta = TableProvider.getTableMeta(entityClass);
        if (tableMeta == null) {
            return null;
        }
        ColumnMeta columnPrimaryKey = tableMeta.getColumnPrimaryKey();
        if (columnPrimaryKey == null) {
            return null;
        }
        Column column = new Column(tableMeta.getTableAlias(), columnPrimaryKey.getColumnName());
        return new Select().loadColumReference().allColumn().from(entityClass).where(whereCondition ->
                whereCondition.andIn(column, pkValues)).fetch().toList();
    }

    public <T> List<T> selectList(String dataSourceName, String sql, Class<T> returnType, ParameterBinder parameterBinder) {
        dataSourceName = checkAndReturnDataSourceName(dataSourceName, returnType);
        SqlStatementSelectWrapper wrapper = new SqlStatementSelectWrapper(
                dataSourceName, new StringBuilder(sql), parameterBinder, returnType);
        FetchableImpl fetchable = new FetchableImpl(wrapper);
        return fetchable.fetch(returnType).toList();
    }

    private String checkAndReturnDataSourceName(String dataSourceName, Class<?> returnType) {
        if (StringUtils.isNotEmpty(dataSourceName)) {
            //校验数据源
            List<String> dataSourceNameList = DataSourceProvider.getDataSourceNameList();
            if (!dataSourceNameList.contains(dataSourceName)) {
                throw new DynamicSqlException(dataSourceName + " does not exist");
            }
            return dataSourceName;
        }
        if (returnType.isAnnotationPresent(Table.class)) {
            TableMeta tableMeta = TableProvider.getTableMeta(returnType);
            return tableMeta.getBindDataSourceName();
        }
        //匹配默认数据源
        return DataSourceProvider.getDefaultDataSourceName();
    }
}
