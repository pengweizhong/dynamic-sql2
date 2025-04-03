package com.dynamic.sql.core.ddl;

import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.model.ColumnMetaData;
import com.dynamic.sql.utils.StringUtils;

import java.security.InvalidParameterException;
import java.util.List;

public class ColumnMetaDataHelper extends MetaDataHelper {
    private final String schemaPattern;
    private final String tableNamePattern;
    private final String columnNamePattern;

    public ColumnMetaDataHelper(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        super(dataSourceName, catalog);
        if (StringUtils.isEmpty(tableNamePattern)) {
            throw new InvalidParameterException("tableNamePattern is invalid");
        }
        this.schemaPattern = schemaPattern;
        this.tableNamePattern = tableNamePattern;
        this.columnNamePattern = columnNamePattern;
    }

    public List<ColumnMetaData> getAllTableMetaData() {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(getDataSourceName(), new StringBuilder(), new ParameterBinder());
        return SqlExecutionFactory.executorSql(DDLType.GET_META, sqlStatementWrapper,
                sqlExecutor -> sqlExecutor.getAllColumnMetaData(getCatalog(), getSchemaPattern(), getTableNamePattern(), getColumnNamePattern()));
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }

    public String getTableNamePattern() {
        return tableNamePattern;
    }

    public String getColumnNamePattern() {
        return columnNamePattern;
    }
}