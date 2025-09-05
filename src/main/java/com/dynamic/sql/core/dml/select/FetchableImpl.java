package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.SqlSelectBuilder;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.utils.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchableImpl implements Fetchable {

    private SqlStatementSelectWrapper sqlStatementSelectWrapper;
    private SelectSpecification selectSpecification;
    private CollectionColumnMapping collectionColumnMapping;
    private final boolean isBuildSqlWrapper;

    public FetchableImpl(SelectSpecification selectSpecification) {
        this.isBuildSqlWrapper = true;
        this.selectSpecification = selectSpecification;
    }

    public FetchableImpl(SqlStatementSelectWrapper sqlStatementSelectWrapper) {
        this.sqlStatementSelectWrapper = sqlStatementSelectWrapper;
        this.isBuildSqlWrapper = false;
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
        if (isBuildSqlWrapper) {
            SqlSelectBuilder sqlSelectBuilder = SqlUtils.matchSqlSelectBuilder(selectSpecification, new HashMap<>());
            sqlStatementSelectWrapper = sqlSelectBuilder.build(returnClass);
            collectionColumnMapping = selectSpecification.getCollectionColumnMapping();
        }
        if (returnClass == null) {
            if (sqlStatementSelectWrapper.getGuessTheTargetClass() == null) {
                throw new IllegalStateException("The query result object cannot be inferred from the context. " +
                        "Please declare the return type.");
            }
            returnClass = (Class<T>) sqlStatementSelectWrapper.getGuessTheTargetClass();
        }
        List<Map<String, Object>> wrapperList = SqlExecutionFactory.executorSql(DMLType.SELECT,
                sqlStatementSelectWrapper, SqlExecutor::executeQuery);
        return new FetchResultImpl<>(returnClass, wrapperList, collectionColumnMapping);
    }
}
