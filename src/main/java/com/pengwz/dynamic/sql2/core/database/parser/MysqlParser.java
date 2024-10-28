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
    private final List<Object> entities;
    private SqlStatementWrapper sqlStatementWrapper;

    public MysqlParser(SchemaProperties schemaProperties, List<Object> entities) {
        super(schemaProperties);
        this.entities = entities;
    }

    @Override
    public SqlStatementWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }


    @Override
    public void insertSelective(Fn<?, ?>[] forcedFields) {
        Object entity = entities.get(0);
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
            if (fieldValue != null || forcedFieldNames.contains(columnMeta.getColumnName())) {
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

    }

    @Override
    public void batchInsert() {

    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return Collections.emptyList();
    }
}
