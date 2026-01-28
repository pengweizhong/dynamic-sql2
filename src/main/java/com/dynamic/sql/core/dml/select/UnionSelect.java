/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.database.SqlExecutionFactory;
import com.dynamic.sql.core.database.SqlExecutor;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.SqlSelectBuilder;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.enums.UnionType;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.SqlUtils;
import com.dynamic.sql.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dynamic.sql.utils.SqlUtils.matchSqlSelectBuilder;

public class UnionSelect extends AbstractUnion {
    private final StringBuilder rawSql = new StringBuilder();
    private final ParameterBinder parameterBinder = new ParameterBinder();
    private final UnionType unionType;
    private NestedMeta nestedMeta;

    public UnionSelect(UnionType unionType) {
        super(new TableRelation<>(new SelectSpecification()));
        this.unionType = unionType;
    }


    public void parseSelectDsls(SelectDsl[] selects) {
        String dataSourceName = null;
        SqlDialect sqlDialect = null;
        Version version = null;
        for (int i = 0; i < selects.length; i++) {
            SelectDsl selectDsl = selects[i];
            rawSql.append("( ");
            Select select = new Select(null);
            AbstractColumnReference columnReference = select.loadColumReference();
            selectDsl.accept(columnReference);
            SqlSelectBuilder sqlSelectBuilder = matchSqlSelectBuilder(select.getSelectSpecification(), new HashMap<>());
            if (StringUtils.isEmpty(dataSourceName)) {
                dataSourceName = sqlSelectBuilder.getDataSourceName();
            }
            if (sqlDialect == null) {
                sqlDialect = sqlSelectBuilder.getSqlDialect();
            }
            if (version == null) {
                version = sqlSelectBuilder.getVersion();
            }
            SqlStatementSelectWrapper build = sqlSelectBuilder.build(null);
            parameterBinder.addParameterBinder(build.getParameterBinder());
            rawSql.append(build.getRawSql());
            rawSql.append(") ");
            // 只有不是最后一个才追加 UNION
            if (i < selects.length - 1) {
                rawSql.append(SqlUtils.getSyntaxUnion(sqlDialect, unionType)).append(" ");
            }
        }
        nestedMeta = new NestedMeta();
        nestedMeta.setVersion(version);
        nestedMeta.setSqlDialect(sqlDialect);
        nestedMeta.setDataSourceName(dataSourceName);
    }


    @Override
    public FetchResult<Object> fetch() {
        throw new UnsupportedOperationException("UnionSelect does not support fetch() without specifying return type. Please use fetch(Class<T> returnClass) instead.");
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        if (returnClass == null) {
            throw new NullPointerException("returnClass is null");
        }
        //检测是否构建了OrderBy
        TableRelation<Object> tableRelation = getTableRelation();
        SelectSpecification selectSpecification = tableRelation.getSelectSpecification();
        //step6 解析order by
        if (CollectionUtils.isNotEmpty(selectSpecification.getOrderBys())) {
            selectSpecification.setNestedMeta(nestedMeta);
            HashMap<String, TableAliasMapping> tableAliasMappingHashMap = new HashMap<>();
            //强制不指定表别名，union语法不允许
            tableAliasMappingHashMap.put("***", new TableAliasMapping(null, true));
            SqlSelectBuilder sqlSelectBuilder = matchSqlSelectBuilder(selectSpecification, tableAliasMappingHashMap);
            sqlSelectBuilder.setIsFromNestedSelect(true);
            rawSql.append(sqlSelectBuilder.parseOrderBy(selectSpecification.getOrderBys(), returnClass));
            SqlStatementSelectWrapper build = sqlSelectBuilder.build(returnClass);
            parameterBinder.addParameterBinder(build.getParameterBinder());
        }
        //step7 解析limit
        if (selectSpecification.getLimitInfo() != null) {
            SqlSelectBuilder sqlSelectBuilder = matchSqlSelectBuilder(selectSpecification, null);
            rawSql.append(sqlSelectBuilder.parseLimit());
            SqlStatementSelectWrapper build = sqlSelectBuilder.build(returnClass);
            parameterBinder.addParameterBinder(build.getParameterBinder());
        }
        SqlStatementSelectWrapper unionSqlStatementSelectWrapper = new SqlStatementSelectWrapper(nestedMeta.getDataSourceName(), rawSql, parameterBinder, returnClass);
        List<Map<String, Object>> wrapperList = SqlExecutionFactory.executorSql(DMLType.SELECT,
                unionSqlStatementSelectWrapper, SqlExecutor::executeQuery);
        return new FetchResultImpl<>(returnClass, wrapperList, null);
    }

    public String getRawSql() {
        return rawSql.toString();
    }

    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }
}
