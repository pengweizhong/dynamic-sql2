package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.database.SqlExecutionFactory;
import com.pengwz.dynamic.sql2.core.database.SqlExecutor;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlSelectBuilder;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementSelectWrapper;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.List;
import java.util.Map;

public class FetchableImpl implements Fetchable {

    private SqlStatementSelectWrapper sqlStatementSelectWrapper;

    public FetchableImpl(SelectSpecification selectSpecification) {
        SqlSelectBuilder sqlSelectBuilder = SqlUtils.matchSqlSelectBuilder(selectSpecification);
        sqlStatementSelectWrapper = sqlSelectBuilder.build();
    }

    @Override
    public <R> FetchResult<R> fetch() {
        return fetchResult(null);
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        return fetchResult(returnClass);
    }

    private <T> FetchResult<T> fetchResult(Class<T> returnClass) {
        if (returnClass == null) {
            throw new IllegalStateException("The query result object cannot be inferred from the context. " +
                    "Please declare the return type.");
        }
        List<Map<String, Object>> wrapperList = SqlExecutionFactory.executorSql(sqlStatementSelectWrapper, SqlExecutor::executeQuery);
        return new FetchResultImpl<>(returnClass, wrapperList);
    }
}
