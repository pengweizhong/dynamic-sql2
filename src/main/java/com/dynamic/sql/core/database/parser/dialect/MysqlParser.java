/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database.parser.dialect;


import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.database.parser.AbstractDialectParser;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.FieldMeta;
import com.dynamic.sql.utils.ConverterUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.SqlUtils;

import java.util.*;

import static com.dynamic.sql.core.dml.SqlStatementWrapper.BatchType.MULTIPLE;
import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public class MysqlParser extends AbstractDialectParser {
    private final Collection<Object> params;
    private SqlStatementWrapper sqlStatementWrapper;

    public MysqlParser(Class<?> entityClass, SchemaProperties schemaProperties,
                       Collection<Object> params, WhereCondition whereCondition) {
        super(entityClass, schemaProperties, whereCondition);
        this.params = params;
    }

    @Override
    public SqlStatementWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }


    @Override
    public void insertSelective(Fn<?, ?>[] forcedFields) {
        parseInsert(InsertType.INSERT_SELECTIVE, forcedFields);
    }


    @Override
    public void insert() {
        parseInsert(InsertType.INSERT, null);
    }

    @Override
    public void insertBatch() {
        parseInsertBatch();
        sqlStatementWrapper.setBatchType(SqlStatementWrapper.BatchType.BATCH);
    }

    @Override
    public void insertMultiple() {
        parseInsertBatch();
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        //继续追加values
        List<ParameterBinder> batchParameterBinders = sqlStatementWrapper.getBatchParameterBinders();
        //从第二个开始，第一个已经拼接过了
        if(batchParameterBinders.size() > 1){
            rawSql.append(", ");
        }
        for (int i = 1; i < batchParameterBinders.size(); i++) {
            ParameterBinder parameterBinder = batchParameterBinders.get(i);
            Collection<Object> values = parameterBinder.getValues();
            rawSql.append("(");
            int j = 0;
            for (Object ignored : values) {//NOSONAR
                rawSql.append("?");
                if (j++ < values.size() - 1) {
                    rawSql.append(", ");
                }
            }
            rawSql.append(")");
            if (i < batchParameterBinders.size() - 1) {
                rawSql.append(", ");
            }
        }
        sqlStatementWrapper.setBatchType(MULTIPLE);
    }

    @Override
    public void upsert(Fn<?, ?>[] forcedFields) {
        upsertEntities(true, forcedFields);
    }

    @Override
    public void upsertMultiple() {
        upsertEntities(false, null);
    }

    @Override
    public void upsertSelective(Fn<?, ?>[] forcedFields) {
        upsertEntities(true, forcedFields);
    }

    @Override
    public void deleteByPrimaryKey() {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" where ");
        ColumnMeta columnMeta = tableMeta.getColumnPrimaryKey();
        if (columnMeta == null) {
            throw new IllegalStateException("The `" + tableMeta.getTableName() + "` table does not declare a primary key value");
        }
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnMeta.getColumnName()));
        if (params.size() > 1) {
            sql.append(" in (");
        } else {
            sql.append(" = ");
        }
        ParameterBinder parameterBinder = new ParameterBinder();
        Iterator<Object> iterator = params.iterator();
        while (iterator.hasNext()) {
            Object param = iterator.next();
            sql.append(registerValueWithKey(parameterBinder, param));
            // 如果还有下一个元素，则添加逗号
            if (iterator.hasNext()) {
                sql.append(", ");
            }
        }
        //集合类追加括号
        if (params.size() > 1) {
            sql.append(")");
        }
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
    }

    @Override
    public void delete() {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableAlias()));
        sql.append(" ");
        sql.append(SqlUtils.getSyntaxAs(schemaProperties.getSqlDialect()));
        sql.append(" ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        if (whereCondition == null) {
            sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, null);
            return;
        }
        sql.append(" where ");
        GenericWhereCondition genericWhereCondition = (GenericWhereCondition) whereCondition;
        String whereConditionSyntax = genericWhereCondition.getWhereConditionSyntax();
        ParameterBinder parameterBinder = genericWhereCondition.getParameterBinder();
        sql.append(whereConditionSyntax);
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
    }

    @Override
    public void updateByPrimaryKey() {
        updateEntityByPrimaryKey(false, null);
    }

    @Override
    public void updateSelectiveByPrimaryKey(Fn<?, ?>[] forcedFields) {
        updateEntityByPrimaryKey(true, forcedFields);
    }

    @Override
    public void update() {
        updateEntities(false, null);
    }

    @Override
    public void updateSelective(Fn<?, ?>[] forcedFields) {
        updateEntities(true, forcedFields);
    }

    private void upsertEntities(boolean isIgnoreNull, Fn<?, ?>[] forcedFields) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        Iterator<Object> entitiesIterator = params.iterator();
        Set<String> forcedFieldNames = getForcedFieldNames(forcedFields);
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<ParameterBinder> parameterBinders = new ArrayList<>();
        boolean isRetrieveColumnNames = true;
        while (entitiesIterator.hasNext()) {
            ParameterBinder parameterBinder = new ParameterBinder();
            Object entity = entitiesIterator.next();
            for (FieldMeta column : tableMeta.getColumnMetas()) {
                Object fieldValue = ReflectUtils.getFieldValue(entity, column.getField());
                //原值为null且没有强制更新null就忽略
                if (fieldValue != null || !isIgnoreNull || forcedFieldNames.contains(column.getField().getName())) {
                    if (isRetrieveColumnNames) {
                        columns.add(column.getColumnName());
                    }
                    registerValueWithKey(parameterBinder, ConverterUtils.convertToDatabaseColumn(SqlDialect.MYSQL, column, fieldValue));
                }
            }
            isRetrieveColumnNames = false;
            if (!parameterBinder.isEmpty()) {
                parameterBinders.add(parameterBinder);
            }
        }
        if (parameterBinders.isEmpty()) {
            throw new IllegalArgumentException("All upsert parameters are null");
        }
        Iterator<String> iterator = columns.iterator();
        sql.append(" (");
        while (iterator.hasNext()) {
            String columnName = iterator.next();
            sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnName));
            if (iterator.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(") values ");
        Iterator<ParameterBinder> parameterBinderIterator = parameterBinders.iterator();
        while (parameterBinderIterator.hasNext()) {
            ParameterBinder parameterBinder = parameterBinderIterator.next();
            sql.append("(");
            Iterator<String> keyIterator = parameterBinder.getKeys().iterator();
            while (keyIterator.hasNext()) {
                keyIterator.next();
                sql.append("?");
                if (keyIterator.hasNext()) {
                    sql.append(", ");
                }
            }
            sql.append(")");
            if (parameterBinderIterator.hasNext()) {
                sql.append(", ");
            }
        }
        //https://dev.mysql.com/doc/relnotes/mysql/8.0/en/news-8-0-20.html
        Version version = new Version(schemaProperties.getMajorVersionNumber(),
                schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
        boolean isNewVersion = version.isGreaterThanOrEqual(new Version(8, 0, 20));
        if (isNewVersion) {
            sql.append(" as _tmp_upsert");
        }
        sql.append(" on duplicate key update ");
        Iterator<String> iteratorName = columns.iterator();
        while (iteratorName.hasNext()) {
            String columnName = SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), iteratorName.next());
            sql.append(columnName);
            if (isNewVersion) {
                sql.append(" = ").append("_tmp_upsert.").append(columnName);
            } else {
                sql.append(" = values(").append(columnName).append(")");
            }
            if (iteratorName.hasNext()) {
                sql.append(", ");
            }
        }
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql);
        sqlStatementWrapper.addBatchParameterBinders(parameterBinders);
        sqlStatementWrapper.setBatchType(SqlStatementWrapper.BatchType.MULTIPLE);
    }

    private void updateEntities(boolean isIgnoreNull, Fn<?, ?>[] forcedFields) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" set ");
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        Iterator<ColumnMeta> iterator = columnMetas.iterator();
        Object entity = params.iterator().next();
        ParameterBinder parameterBinder = new ParameterBinder();
        Set<String> forcedFieldNames = getForcedFieldNames(forcedFields);
        while (iterator.hasNext()) {
            ColumnMeta column = iterator.next();
            Object fieldValue = ReflectUtils.getFieldValue(entity, column.getField());
            //原值为null且没有强制更新null就忽略
            if (fieldValue != null || !isIgnoreNull || forcedFieldNames.contains(column.getField().getName())) {
                sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), column.getColumnName()));
                sql.append(" = ");
                Object param = ConverterUtils.convertToDatabaseColumn(SqlDialect.MYSQL, column, fieldValue);
                sql.append(registerValueWithKey(parameterBinder, param));
                sql.append(", ");
            }
        }
        if (parameterBinder.isEmpty()) {
            throw new IllegalStateException("The `" + tableMeta.getTableName() + "` table has no columns that need to be updated");
        }
        sql.setLength(sql.length() - 2);
        if (whereCondition == null) {
            sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
            return;
        }
        sql.append(" where ");
        GenericWhereCondition genericWhereCondition = (GenericWhereCondition) whereCondition;
        String whereConditionSyntax = genericWhereCondition.getWhereConditionSyntax();
        parameterBinder.addParameterBinder(genericWhereCondition.getParameterBinder());
        sql.append(whereConditionSyntax);
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
    }

    private void updateEntityByPrimaryKey(boolean isIgnoreNull, Fn<?, ?>[] forcedFields) {
        ColumnMeta columnPrimaryKey = tableMeta.getColumnPrimaryKey();
        if (columnPrimaryKey == null) {
            throw new IllegalStateException("The `" + tableMeta.getTableName() + "` table does not declare a primary key value");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("update ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" set ");
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        Iterator<ColumnMeta> iterator = columnMetas.iterator();
        Object entity = params.iterator().next();
        ParameterBinder parameterBinder = new ParameterBinder();
        Set<String> forcedFieldNames = getForcedFieldNames(forcedFields);
        while (iterator.hasNext()) {
            ColumnMeta column = iterator.next();
            //不需要更新已知的主键
            if (column.equals(columnPrimaryKey)) {
                continue;
            }
            Object fieldValue = ReflectUtils.getFieldValue(entity, column.getField());
            //原值为null且没有强制更新null就忽略
            if (fieldValue != null || !isIgnoreNull || forcedFieldNames.contains(column.getField().getName())) {
                sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), column.getColumnName()));
                sql.append(" = ");
                Object param = ConverterUtils.convertToDatabaseColumn(SqlDialect.MYSQL, column, fieldValue);
                sql.append(registerValueWithKey(parameterBinder, param));
                sql.append(", ");
            }
        }
        if (parameterBinder.isEmpty()) {
            throw new IllegalStateException("The `" + tableMeta.getTableName() + "` table has no columns that need to be updated");
        }
        sql.setLength(sql.length() - 2);
        sql.append(" where ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnPrimaryKey.getColumnName()));
        sql.append(" = ");
        Object fieldValue = ReflectUtils.getFieldValue(entity, columnPrimaryKey.getField());
        Object param = ConverterUtils.convertToDatabaseColumn(SqlDialect.MYSQL, columnPrimaryKey, fieldValue);
        sql.append(registerValueWithKey(parameterBinder, param));
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
    }

    enum InsertType {
        INSERT, INSERT_SELECTIVE;
    }


    private void parseInsert(InsertType insertType, Fn<?, ?>[] forcedFields) {
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        Set<String> forcedFieldNames = getForcedFieldNames(forcedFields);
        ArrayList<Object> values = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" (");
        Object entity = params.iterator().next();
        ArrayList<String> insertColumns = new ArrayList<>();
        for (ColumnMeta columnMeta : columnMetas) {
            Object fieldValue = ReflectUtils.getFieldValue(entity, columnMeta.getField());
            if (fieldValue != null || insertType == InsertType.INSERT
                    || forcedFieldNames.contains(columnMeta.getColumnName())) {
                insertColumns.add(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnMeta.getColumnName()));
                values.add(ConverterUtils.convertToDatabaseColumn(SqlDialect.MYSQL, columnMeta, fieldValue));
            }
        }
        if (values.isEmpty()) {
            throw new IllegalStateException("No non-null attribute fields were provided.");
        }
        sql.append(String.join(", ", insertColumns));
        sql.append(") values (");
        ParameterBinder parameterBinder = new ParameterBinder();
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            sql.append(registerValueWithKey(parameterBinder, value));
            if (i < values.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
    }

    private void parseInsertBatch() {
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" (");
        StringBuilder placeHolders = new StringBuilder();
        for (int i = 0; i < columnMetas.size(); i++) {
            ColumnMeta columnMeta = columnMetas.get(i);
            sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnMeta.getColumnName()));
            placeHolders.append("?");
            if (i < columnMetas.size() - 1) {
                sql.append(", ");
                placeHolders.append(", ");
            }
        }
        sql.append(") values (").append(placeHolders).append(")");
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql);
        for (Object entity : params) {
            ParameterBinder parameterBinder = new ParameterBinder();
            for (ColumnMeta columnMeta : columnMetas) {
                Object fieldValue = ReflectUtils.getFieldValue(entity, columnMeta.getField());
                registerValueWithKey(parameterBinder, ConverterUtils.convertToDatabaseColumn(SqlDialect.MYSQL, columnMeta, fieldValue));
            }
            sqlStatementWrapper.addBatchParameterBinder(parameterBinder);
        }
    }

    private Set<String> getForcedFieldNames(Fn<?, ?>[] forcedFields) {
        Set<String> forcedFieldNames = new HashSet<>();
        if (forcedFields == null || forcedFields.length < 1) {
            return forcedFieldNames;
        }
        for (Fn fn : forcedFields) {
            String fieldName = ReflectUtils.fnToFieldName(fn);
            forcedFieldNames.add(fieldName);
        }
        return forcedFieldNames;
    }

}
