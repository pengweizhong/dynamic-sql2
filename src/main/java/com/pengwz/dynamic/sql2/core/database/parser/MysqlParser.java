package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.GenericWhereCondition;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper.BatchType;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.*;

import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

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
        sqlStatementWrapper.setBatchType(BatchType.BATCH);
    }

    @Override
    public void insertMultiple() {
        parseInsertBatch();
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        //继续追加values
        List<ParameterBinder> batchParameterBinders = sqlStatementWrapper.getBatchParameterBinders();
        //从第二个开始，第一个已经拼接过了
        rawSql.append(", ");
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
        sqlStatementWrapper.setBatchType(BatchType.MULTIPLE);
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
        for (int i = 0; i < columnMetas.size(); i++) {
            ColumnMeta columnMeta = columnMetas.get(i);
            Object fieldValue = ReflectUtils.getFieldValue(entity, columnMeta.getField());
            if (fieldValue != null || insertType == InsertType.INSERT
                    || forcedFieldNames.contains(columnMeta.getColumnName())) {
                sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnMeta.getColumnName()));
                values.add(ConverterUtils.convertToDatabaseColumn(columnMeta, fieldValue));
                if (i < columnMetas.size() - 1) {
                    sql.append(", ");
                }
            }
        }
        if (values.isEmpty()) {
            throw new IllegalStateException("No non-null attribute fields were provided.");
        }
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
                registerValueWithKey(parameterBinder, ConverterUtils.convertToDatabaseColumn(columnMeta, fieldValue));
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
