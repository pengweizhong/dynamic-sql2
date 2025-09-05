/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.pagination.impl;


import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.plugins.pagination.AbstractPage;
import com.dynamic.sql.plugins.pagination.ConditionPageInfo;
import com.dynamic.sql.plugins.pagination.DialectPagination;
import com.dynamic.sql.utils.SqlUtils;

import java.util.HashMap;
import java.util.Map;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;

public class MySQLDialectPagination implements DialectPagination {
    @Override
    public StringBuilder selectCountSql(Version version, SqlStatementWrapper sqlStatementWrapper, AbstractPage abstractPage) {
        StringBuilder selectCountSql = new StringBuilder();
        selectCountSql.append("select count(1) from (");
        if (abstractPage instanceof ConditionPageInfo) {
            ConditionPageInfo conditionPageInfo = (ConditionPageInfo) abstractPage;
            StringBuilder selectCountAppendWhereSql = new StringBuilder(sqlStatementWrapper.getRawSql());
            selectCountAppendWhereSql.insert(0, "select * from (");
            selectCountAppendWhereSql.append(") _append_count_page_temp ");
            Map<String, TableAliasMapping> aliasMap = new HashMap<>();
            aliasMap.put("***", new TableAliasMapping("_append_count_page_temp", true));
            GenericWhereCondition whereCondition =
                    SqlUtils.matchDialectCondition(SqlDialect.MYSQL, version, aliasMap, sqlStatementWrapper.getDataSourceName());
            conditionPageInfo.getAppendWhere().accept(whereCondition);
            String whereConditionSyntax = whereCondition.getWhereConditionSyntax();
            selectCountAppendWhereSql.append(" where ").append(whereConditionSyntax);
            ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
            sqlStatementWrapper.getParameterBinder().addParameterBinder(whereParameterBinder);
            selectCountSql.append(selectCountAppendWhereSql);
        } else {
            selectCountSql.append(sqlStatementWrapper.getRawSql());
        }
        selectCountSql.append(") _count_page_temp ");
        return selectCountSql;
    }

    @Override
    public void modifyPagingSql(Version version, SqlStatementWrapper sqlStatementWrapper, AbstractPage abstractPage) {
        //修改原来的SQL，加上limit分页
        StringBuilder selectPageSql = sqlStatementWrapper.getRawSql();
        selectPageSql.insert(0, "select * from (");
        selectPageSql.append(") _page_temp ");
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        //添加追加where条件的逻辑
        if (abstractPage instanceof ConditionPageInfo) {
            ConditionPageInfo conditionPageInfo = (ConditionPageInfo) abstractPage;
            Map<String, TableAliasMapping> aliasMap = new HashMap<>();
            aliasMap.put("**", new TableAliasMapping("_page_temp", true));
            GenericWhereCondition whereCondition =
                    SqlUtils.matchDialectCondition(SqlDialect.MYSQL, version, aliasMap, sqlStatementWrapper.getDataSourceName());
            conditionPageInfo.getAppendWhere().accept(whereCondition);
            String whereConditionSyntax = whereCondition.getWhereConditionSyntax();
            selectPageSql.append(" where ").append(whereConditionSyntax);
            ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
            parameterBinder.addParameterBinder(whereParameterBinder);
        }
        // 计算分页的偏移量 (pageIndex - 1) * pageSize
        int offset = (abstractPage.getPageIndex() - 1) * abstractPage.getPageSize();
        String offsetKey = registerValueWithKey(parameterBinder, offset);
        String pageSizeKey = registerValueWithKey(parameterBinder, abstractPage.getPageSize());
        // 最后加上分页的 LIMIT 子句
        selectPageSql.append(" limit ");
        selectPageSql.append(offsetKey);
        selectPageSql.append(", ");
        selectPageSql.append(pageSizeKey);

    }
    // if (conditionPageInfo.getCacheAppendWhereSql() != null) {
    //                StringBuilder selectCountAppendWhereSql = new StringBuilder(sqlStatementWrapper.getRawSql());
    //                selectCountAppendWhereSql.insert(0, "select * from (");
    //                selectCountAppendWhereSql.append(") _append_count_page_temp ");
    //                Map<String, String> aliasMap = new HashMap<>();
    //                aliasMap.put("**", "_append_count_page_temp");
    //                GenericWhereCondition whereCondition =
    //                        SqlUtils.matchDialectCondition(SqlDialect.MYSQL, version, aliasMap, sqlStatementWrapper.getDataSourceName());
    //                conditionPageInfo.getAppendWhere().accept(whereCondition);
    //                String whereConditionSyntax = whereCondition.getWhereConditionSyntax();
    //                selectCountAppendWhereSql.append(" where ").append(whereConditionSyntax);
    //                ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
    //                conditionPageInfo.setCacheAppendWhereSql(selectCountAppendWhereSql);
    //                sqlStatementWrapper.getParameterBinder().addParameterBinder(whereParameterBinder);
    //                selectCountSql.append(selectCountAppendWhereSql);
    //                conditionPageInfo.setCacheParameterBinder(whereParameterBinder);
    //            } else {
    //
    //            }
}
