package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;

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
}
