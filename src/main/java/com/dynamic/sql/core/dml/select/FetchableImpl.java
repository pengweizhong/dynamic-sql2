package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.SqlSelectBuilder;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.utils.SqlUtils;

import java.util.List;
import java.util.Map;

public class FetchableImpl implements Fetchable {

    private SqlStatementSelectWrapper sqlStatementSelectWrapper;

    public FetchableImpl(SelectSpecification selectSpecification) {
        SqlSelectBuilder sqlSelectBuilder = SqlUtils.matchSqlSelectBuilder(selectSpecification);
        sqlStatementSelectWrapper = sqlSelectBuilder.build();
    }

    public FetchableImpl(SqlStatementSelectWrapper sqlStatementSelectWrapper) {
        this.sqlStatementSelectWrapper = sqlStatementSelectWrapper;
    }

    @Override
    public <R> FetchResult<R> fetch() {
        return fetchResult(null);
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        return fetchResult(returnClass);
    }

    @SuppressWarnings("unchecked")
    private <T> FetchResult<T> fetchResult(Class<T> returnClass) {
        if (returnClass == null) {
            if (sqlStatementSelectWrapper.getGuessTheTargetClass() == null) {
                throw new IllegalStateException("The query result object cannot be inferred from the context. " +
                        "Please declare the return type.");
            }
            returnClass = (Class<T>) sqlStatementSelectWrapper.getGuessTheTargetClass();
        }
        List<Map<String, Object>> wrapperList = SqlExecutionFactory.executorSql(DMLType.SELECT,
                sqlStatementSelectWrapper, SqlExecutor::executeQuery);
        return new FetchResultImpl<>(returnClass, wrapperList);
    }
}
