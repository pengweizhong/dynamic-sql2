package com.dynamic.sql.core.ddl;

import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.enums.DMLType;

import java.util.function.Function;

public class SqlStatement {
    //数据源名称
    private String dataSourceName;
    //需要执行的任意SQL
    private String sql;
    //SQL对应的待编译的参数
    private ParameterBinder parameterBinder;

    public SqlStatement(String dataSourceName, String sql, ParameterBinder parameterBinder) {
        this.dataSourceName = dataSourceName;
        this.sql = sql.trim();
        this.parameterBinder = parameterBinder;
    }

    public String getSql() {
        return sql;
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    public String getDataSourceName() {
        return dataSourceName == null ? DataSourceProvider.getDefaultDataSourceName() : dataSourceName;
    }

    public Object execute() {
        SqlStatementWrapper sqlStatementWrapper = new SqlStatementWrapper(getDataSourceName(), new StringBuilder(getSql()), getParameterBinder());
        Function<SqlExecutor, Object> doSqlExecutor;
        String type = getSql().substring(0, getSql().indexOf(" "));
        if (type.equalsIgnoreCase("select")) {
            doSqlExecutor = SqlExecutor::executeQuery;
        } else {
            doSqlExecutor = SqlExecutor::update;
        }
        return SqlExecutionFactory.executorSql(DMLType.UPDATE, sqlStatementWrapper, doSqlExecutor);
    }
}
