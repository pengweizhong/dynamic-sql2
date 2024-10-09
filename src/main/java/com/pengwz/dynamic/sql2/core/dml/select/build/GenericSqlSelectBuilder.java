package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.NumberColumn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.NestedColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class GenericSqlSelectBuilder extends SqlSelectBuilder {

    public GenericSqlSelectBuilder(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    protected void parseColumnFunction() {
        List<ColumnQuery> columFunctions1 = selectSpecification.getColumFunctions();
        sqlBuilder.append(SqlUtils.getSyntaxSelect(sqlDialect)).append(" ");
        for (int i = 0; i < columFunctions1.size(); i++) {
            ColumnQuery columnQuery = columFunctions1.get(i);
            String columnSeparator = "";
            //最后一个列后面不追加逗号
            if (columFunctions1.size() - 1 != i) {
                columnSeparator = ", ";
            }
            if (columnQuery instanceof FunctionColumn) {
                FunctionColumn functionColumn = (FunctionColumn) columnQuery;
                ColumFunction columFunction = functionColumn.getColumFunction();
                //数字列不需要关心别名问题
                if (columFunction instanceof NumberColumn) {
                    sqlBuilder.append(columFunction.getFunctionToString(sqlDialect, version)).append(columnSeparator);
                    continue;
                }
                Fn<?, ?> fn = columFunction.getOriginColumnFn();
                String tableAlias = functionColumn.getTableAlias();
                if(tableAlias == null){
                    tableAlias = aliasTableMap.get(ReflectUtils.getOriginalClassCanonicalName(fn));
                }
                columFunction.setTableAlias(tableAlias);
                String functionToString = columFunction.getFunctionToString(sqlDialect, version);
                String fieldName = ReflectUtils.fnToFieldName(fn);
                //拼接别名，
                String columnAlias = StringUtils.isEmpty(columnQuery.getAlias()) ? fieldName : columnQuery.getAlias();
                System.out.println("测试函数列输出结果 ---> " + functionToString);
                sqlBuilder.append(functionToString).append(syntaxAs()).append(columnAlias).append(columnSeparator);
                parameterBinder.addParameterBinder(columFunction.getParameterBinder());
            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                SqlSelectParam sqlSelectParam = SqlUtils.executeNestedSelect(nestedColumn.getNestedSelect());
                String columnAliasString = syntaxAs() + columnQuery.getAlias();
                sqlBuilder.append("(").append(sqlSelectParam.getRawSql()).append(")").append(columnAliasString).append(columnSeparator);
                parameterBinder.addParameterBinder(sqlSelectParam.getParameterBinder());
            }
        }
    }

    /**
     * 返回库名+表名+别名的结果
     */
    protected String automaticallySelectAliases(JoinTable joinTable) {
        Class<?> tableClass = joinTable.getTableClass();
        TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
        String alias = StringUtils.isEmpty(joinTable.getTableAlias()) ? tableMeta.getTableAlias() : joinTable.getTableAlias();
        String tableAlias = SqlUtils.quoteIdentifier(sqlDialect, alias);
        return getSchemaName(tableMeta).concat(syntaxAs()).concat(tableAlias);
    }

    @Override
    protected void parseFormTables() {
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        for (JoinTable joinTable : joinTables) {
            parseFormTable(joinTable);
        }
    }

    protected void parseFormTable(JoinTable joinTable) {
        parseFormTable(joinTable, this.sqlBuilder);
    }

    protected void parseFormTable(JoinTable joinTable, StringBuilder sqlBuilder) {
        if (joinTable instanceof FromJoin) {
            sqlBuilder.append(automaticallySelectAliases(joinTable));
            return;
        }
        String syntaxJoin = " " + SqlUtils.getSyntaxJoin(sqlDialect, joinTable) + " ";
        String syntaxOn = " " + SqlUtils.getSyntaxOn(sqlDialect) + " ";
        // INNER, LEFT, RIGHT, FULL, CROSS, SELF;
        sqlBuilder.append(syntaxJoin).append(automaticallySelectAliases(joinTable));
        //拼接On条件
        Consumer<Condition> onCondition = joinTable.getOnCondition();
        if (onCondition != null) {
            WhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, aliasTableMap);
            onCondition.accept(whereCondition);
            parameterBinder.addParameterBinder(whereCondition.getParameterBinder());
            sqlBuilder.append(syntaxOn).append(whereCondition.getWhereConditionSyntax());
        }
    }

    public void parseLimit() {
        LimitInfo limitInfo = selectSpecification.getLimitInfo();
        if (limitInfo == null) {
            return;
        }
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxLimit(SqlDialect.MYSQL)).append(" ");
        if (limitInfo.getOffset() != null) {
            sqlBuilder.append(parameterBinder.registerValueWithKey(limitInfo.getOffset())).append(", ");

        }
        sqlBuilder.append(parameterBinder.registerValueWithKey(limitInfo.getLimit()));
    }

}
