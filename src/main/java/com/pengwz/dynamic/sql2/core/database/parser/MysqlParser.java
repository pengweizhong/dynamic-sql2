package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.*;

public class MysqlParser extends AbstractDialectParser {
    private final Collection<Object> entities;
    private SqlStatementWrapper sqlStatementWrapper;

    public MysqlParser(SchemaProperties schemaProperties, Collection<Object> entities) {
        super(schemaProperties);
        this.entities = entities;
    }

    @Override
    public SqlStatementWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }


    @Override
    public void insertSelective(Fn<?, ?>[] forcedFields) {
        parseInsert(InsertType.INSERT_SELECTIVE, forcedFields);
    }

    private void parseInsert(InsertType insertType, Fn<?, ?>[] forcedFields) {
        Object entity = entities.iterator().next();
        TableMeta tableMeta = TableProvider.getTableMeta(entity.getClass());
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        Set<String> forcedFieldNames = getForcedFieldNames(forcedFields);
        ArrayList<Object> values = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" (");
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
            sql.append(parameterBinder.registerValueWithKey(value));
            if (i < values.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinder);
    }

    private void parseInsertBatch() {
//        Object firstEntity = entities.iterator().next();
//        TableMeta tableMeta = TableProvider.getTableMeta(firstEntity.getClass());
//        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
//        StringBuilder sql = new StringBuilder();
//        sql.append("insert into ");
//        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
//        sql.append(" (");
//        StringBuilder placeHolders = new StringBuilder();
//        for (int i = 0; i < columnMetas.size(); i++) {
//            ColumnMeta columnMeta = columnMetas.get(i);
//            sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnMeta.getColumnName()));
//            placeHolders.append("?");
//            if (i < columnMetas.size() - 1) {
//                sql.append(", ");
//                placeHolders.append(", ");
//            }
//        }
//        sql.append(") values (").append(placeHolders).append(")");
//        ArrayList<ParameterBinder> parameterBinders = new ArrayList<>();
//        for (Object entity : entities) {
//            ParameterBinder parameterBinder = new ParameterBinder();
//            for (ColumnMeta columnMeta : columnMetas) {
//                Object fieldValue = ReflectUtils.getFieldValue(entity, columnMeta.getField());
//                parameterBinder.registerValueWithKey(ConverterUtils.convertToDatabaseColumn(columnMeta, fieldValue));
//            }
//            parameterBinders.add(parameterBinder);
//        }
////        sqlStatementWrapper = new SqlStatementWrapper(schemaProperties.getDataSourceName(), sql, parameterBinders);
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


    @Override
    public void insert() {
        parseInsert(InsertType.INSERT, null);
    }

    @Override
    public void insertBatch() {
        parseInsertBatch();
    }


    @Override
    public List<Map<String, Object>> executeQuery() {
        return Collections.emptyList();
    }

    enum InsertType {
        INSERT, INSERT_SELECTIVE;
    }
}
