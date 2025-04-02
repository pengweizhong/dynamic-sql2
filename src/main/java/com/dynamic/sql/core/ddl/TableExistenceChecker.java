package com.dynamic.sql.core.ddl;

import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.model.TableMetaData;
import com.dynamic.sql.utils.StringUtils;

import java.security.InvalidParameterException;
import java.util.List;

public class TableExistenceChecker {
    private final String dataSourceName;
    private final String catalog;
    private final String schemaPattern;
    private final String tableNamePattern;
    private final String[] tableTypes;

    public TableExistenceChecker(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes) {
        if (StringUtils.isEmpty(tableNamePattern)) {
            throw new InvalidParameterException("tableNamePattern is invalid");
        }
        this.dataSourceName = dataSourceName == null ? DataSourceProvider.getDefaultDataSourceName() : dataSourceName;
        this.catalog = catalog;
        this.schemaPattern = schemaPattern;
        this.tableNamePattern = tableNamePattern;
        this.tableTypes = tableTypes;
    }

    public List<TableMetaData> getAllTableMetaData() {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(dataSourceName, new StringBuilder(), new ParameterBinder());
        return SqlExecutionFactory.executorSql(DDLType.GET_META, sqlStatementWrapper,
                sqlExecutor -> sqlExecutor.getAllTableMetaData(this.catalog, schemaPattern, tableNamePattern, tableTypes));
    }
}
