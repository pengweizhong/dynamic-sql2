package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.dml.select.build.join.FullJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.LeftJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.RightJoin;

import java.util.List;

public class MysqlSqlSelectBuilder extends GenericSqlSelectBuilder {
    private String bakBuilder;

    public MysqlSqlSelectBuilder(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    @Override
    protected void parseFormTables() {
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        joinTables.forEach(joinTable -> {
            super.parseJoinTable(joinTable, this.sqlBuilder);
        });
//        StringBuilder leftBuilder = new StringBuilder();
//        StringBuilder rightBuilder = new StringBuilder();
//        StringBuilder stringBuilder = new StringBuilder();
//        for (JoinTable joinTable : joinTables) {
//            if (joinTable instanceof FromJoin) {
//                sqlBuilder.append(automaticallySelectAliases(joinTable));
//                continue;
//            }
//            if (!(joinTable instanceof FullJoin)) {
//                super.parseFormTable(joinTable, stringBuilder);
//                this.sqlBuilder.append(stringBuilder);
//                continue;
//            }
//            parseLeftFullJoin((FullJoin) joinTable, leftBuilder);
//            parseRightFullJoin((FullJoin) joinTable, rightBuilder);
//        }
//        sqlBuilder.setLength(0);
//        sqlBuilder.append(leftBuilder.append(stringBuilder)).append(" union ").append(rightBuilder.append(stringBuilder));
    }

    //MySQL 不支持原生的 FULL JOIN，所以需要使用 LEFT JOIN 和 UNION 来实现。
    //如果触发了全连接，则需要对整个表对象先进行串联
    //优先保证可用性，而不考虑性能问题
    protected void parseLeftFullJoin(FullJoin fullJoin, StringBuilder leftBuilder) {
        //转成左右关联对象
        LeftJoin leftJoin = new LeftJoin(fullJoin.getTableClass(), fullJoin.getTableAlias(), fullJoin.getOnCondition());
//        bakBuilder = String.valueOf(sqlBuilder);
        leftBuilder.append(sqlBuilder);
        super.parseJoinTable(leftJoin, leftBuilder);
    }

    protected void parseRightFullJoin(FullJoin fullJoin, StringBuilder rightBuilder) {
        //转成左右关联对象
        RightJoin rightJoin = new RightJoin(fullJoin.getTableClass(), fullJoin.getTableAlias(), fullJoin.getOnCondition());
//        //左连接
        rightBuilder.append(sqlBuilder);
        super.parseJoinTable(rightJoin, rightBuilder);
    }
}
