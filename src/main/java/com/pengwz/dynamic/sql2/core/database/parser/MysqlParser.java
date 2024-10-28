package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlParser extends AbstractDialectParser {
    private final List<Object> entities;

    public MysqlParser(SchemaProperties schemaProperties, List<Object> entities) {
        super(schemaProperties);
        this.entities = entities;
    }

    @Override
    public SqlStatementWrapper getSqlStatementWrapper() {

        return null;
    }


    @Override
    public void insertSelective() {
        insertSelective(null);
    }

    @Override
    public <T, F> void insertSelective(Collection<Fn<T, F>> forcedFields) {
        Object entity = entities.get(0);
        TableMeta tableMeta = TableProvider.getTableMeta(entity.getClass());
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        Set<String> forcedFieldNames = getForcedFieldNames(forcedFields);
        Map<String, Object> insertColumnMap = new HashMap<>();
        for (ColumnMeta columnMeta : columnMetas) {
            Object fieldValue = ReflectUtils.getFieldValue(entity, columnMeta.getField());
            if (fieldValue != null) {
                ConverterUtils.convertToDatabaseColumn(columnMeta,fieldValue);
                insertColumnMap.put(columnMeta.getColumnName(), fieldValue);
            } else {
                if (forcedFieldNames.contains(columnMeta.getColumnName())) {
                    insertColumnMap.put(columnMeta.getColumnName(), null);
                }
            }
        }
        if (insertColumnMap.isEmpty()) {
            throw new IllegalStateException("No non-null attribute fields were provided.");
        }
        AtomicInteger atomicInteger = new AtomicInteger(insertColumnMap.size());
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), tableMeta.getTableName()));
        sql.append(" (");
        List<Object> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : insertColumnMap.entrySet()) {
            atomicInteger.decrementAndGet();
            String columnName = entry.getKey();
            Object value = entry.getValue();
//            sql.append(SqlUtils.quoteIdentifier(schemaProperties.getSqlDialect(), columnName));
//            if (i != columnMetas.size() - 1) {
//                sql.append(",");
//            }
        }
        sql.append(") ");

    }

    private <T, F> Set<String> getForcedFieldNames(Collection<Fn<T, F>> forcedFields) {
        Set<String> forcedFieldNames = new HashSet<>();
        if (CollectionUtils.isEmpty(forcedFields)) {
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

    }

    @Override
    public void batchInsert() {

    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return Collections.emptyList();
    }
}
