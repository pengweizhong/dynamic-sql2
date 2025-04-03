package com.dynamic.sql.core.ddl;

import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.model.TableMetaData;
import com.dynamic.sql.utils.StringUtils;

import java.security.InvalidParameterException;
import java.util.List;

public class TableMetaDataHelper extends MetaDataHelper {
    private final String schemaPattern;
    private final String tableNamePattern;
    private final String[] tableTypes;

    public TableMetaDataHelper(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes) {
        super(dataSourceName, catalog);
        if (StringUtils.isEmpty(tableNamePattern)) {
            throw new InvalidParameterException("tableNamePattern is invalid");
        }
        this.schemaPattern = schemaPattern;
        this.tableNamePattern = tableNamePattern;
        this.tableTypes = tableTypes;
    }

    public List<TableMetaData> getAllTableMetaData() {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(getDataSourceName(), new StringBuilder(), new ParameterBinder());
        return SqlExecutionFactory.executorSql(DDLType.GET_META, sqlStatementWrapper,
                sqlExecutor -> sqlExecutor.getAllTableMetaData(getCatalog(), getSchemaPattern(), getTableNamePattern(), getTableTypes()));
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }

    public String getTableNamePattern() {
        return tableNamePattern;
    }

    public String[] getTableTypes() {
        return tableTypes;
    }
}