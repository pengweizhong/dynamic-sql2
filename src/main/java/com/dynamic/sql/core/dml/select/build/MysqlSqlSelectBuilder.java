/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build;


import com.dynamic.sql.core.dml.select.build.join.FullJoin;
import com.dynamic.sql.core.dml.select.build.join.JoinTable;
import com.dynamic.sql.core.dml.select.build.join.LeftJoin;
import com.dynamic.sql.core.dml.select.build.join.RightJoin;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.List;
import java.util.Map;

public class MysqlSqlSelectBuilder extends GenericSqlSelectBuilder {

    public MysqlSqlSelectBuilder(SelectSpecification selectSpecification, Map<String, TableAliasMapping> aliasTableMap) {
        super(selectSpecification, aliasTableMap);
    }

    @Override
    protected boolean parseFormTables() {
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        //先判断有没有全连接
        if (joinTables.stream().noneMatch(FullJoin.class::isInstance)) {
            super.parseFormTables();
            return true;
        }
        //处于性能考虑、实现难度、引用场景等，Mysql下全连接只支持两个表
        if (joinTables.size() != 2) {
            throw new IllegalStateException("MySQL full join only supports two tables.");
        }
        //先解析Form
        parseJoinTable(joinTables.get(0));
        //将全连接两边的表转为左右连接
        JoinTable joinTable = joinTables.get(1);
        //保留原始SQL
        final StringBuilder stubSql = new StringBuilder(sqlBuilder);
        LeftJoin leftJoin;
        RightJoin rightJoin;
        if (joinTable.getTableClass() != null) {
            leftJoin = new LeftJoin(joinTable.getTableClass(), joinTable.getTableAlias(), joinTable.getOnCondition());
            rightJoin = new RightJoin(joinTable.getTableClass(), joinTable.getTableAlias(), joinTable.getOnCondition());
        } else {
            leftJoin = new LeftJoin(joinTable.getCteTable(), joinTable.getOnCondition());
            rightJoin = new RightJoin(joinTable.getCteTable(), joinTable.getOnCondition());
        }
        //解析左连接
        parseJoinTable(leftJoin);
        continueParsingSql();
        final StringBuilder leftSql = new StringBuilder(sqlBuilder);
        //清空原始SQL
        sqlBuilder.setLength(0);
        final StringBuilder rightSql = new StringBuilder(stubSql);
        parseJoinTable(rightJoin);
        continueParsingSql();
        rightSql.append(sqlBuilder);
        //union 合并SQL
        leftSql.append(" union ").append(rightSql);
        sqlBuilder.setLength(0);
        sqlBuilder.append(leftSql);
        return false;
    }

}
