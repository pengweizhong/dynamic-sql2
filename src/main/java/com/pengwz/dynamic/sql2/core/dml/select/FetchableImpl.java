package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.LogProperties;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlSelectBuilder;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

public class FetchableImpl implements Fetchable {
    private SqlStatementWrapper sqlStatementWrapper;

    public FetchableImpl(SelectSpecification selectSpecification) {
        SqlSelectBuilder sqlSelectBuilder = SqlUtils.matchSqlSelectBuilder(selectSpecification);
        this.sqlStatementWrapper = sqlSelectBuilder.build();
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(sqlSelectBuilder.getDataSourceName());
        if (schemaProperties.isPrintSql()) {
            SqlLogger sqlLogger = LogProperties.getInstance().getSqlLogger();
            sqlLogger.logSql(sqlStatementWrapper);
        }
    }

    @Override
    public <R> FetchResult<R> fetch() {
        return new FetchResultImpl<>(null);
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        return new FetchResultImpl<>(returnClass);
    }
}
