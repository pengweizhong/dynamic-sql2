package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.anno.Table;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.table.view.ViewMeta;
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
        if (StringUtils.isEmpty(dataSourceName)) {
            if (returnType.isAnnotationPresent(Table.class)) {
                TableMeta tableMeta = TableProvider.getTableMeta(returnType);
                dataSourceName = tableMeta.getBindDataSourceName();
            } else {
                //判断是否为原始对象
                if (returnType.getClassLoader() == null) {
                    throw new IllegalArgumentException("Unable to match data source according to class: " + returnType.getName()
                            + ", need to declare data source name");
                }
                ViewMeta viewMeta = TableProvider.getViewMeta(returnType);
                dataSourceName = viewMeta.getBindDataSourceName();
            }
        } else {
            //校验数据源
            List<String> dataSourceNameList = DataSourceProvider.getDataSourceNameList();
            if (!dataSourceNameList.contains(dataSourceName)) {
                throw new IllegalArgumentException(dataSourceName + " does not exist");
            }
        }
        SqlStatementSelectWrapper wrapper = new SqlStatementSelectWrapper(
                dataSourceName, new StringBuilder(sql), parameterBinder, returnType);
        FetchableImpl fetchable = new FetchableImpl(wrapper);
        return fetchable.fetch(returnType).toList();
    }
}
