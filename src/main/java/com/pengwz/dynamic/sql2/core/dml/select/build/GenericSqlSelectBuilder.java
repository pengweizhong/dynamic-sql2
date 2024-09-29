package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.NumberColumn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.NestedColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.InnerJoin;
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
                String functionToString = columFunction.getFunctionToString(sqlDialect, version);
                //数字列不需要关心别名问题
                if (columFunction instanceof NumberColumn) {
                    sqlBuilder.append(functionToString).append(columnSeparator);
                    continue;
                }
                Fn<?, ?> fn = columFunction.getoriginColumnFn();
                String fieldName = ReflectUtils.fnToFieldName(fn);
                //拼接别名，
                String columnAlias = StringUtils.isEmpty(columnQuery.getAlias()) ? fieldName : columnQuery.getAlias();
                System.out.println("测试函数列输出结果 ---> " + functionToString);
                sqlBuilder.append(functionToString).append(syntaxAs()).append(columnAlias).append(columnSeparator);
                parameterBinder.addParameterBinder(columFunction.getParameterBinder());
            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                Consumer<NestedSelect> nestedSelectConsumer = nestedColumn.getNestedSelect();
                NestedSelect nestedSelect = new NestedSelect();
                nestedSelectConsumer.accept(nestedSelect);
                SqlSelectBuilder nestedSqlBuilder = new GenericSqlSelectBuilder(nestedSelect.getSelectSpecification());
                SqlSelectParam sqlSelectParam = nestedSqlBuilder.build();
                System.out.println("测试内嵌列输出结果 ---> " + sqlSelectParam.getSql());
                String columnAliasString = syntaxAs() + columnQuery.getAlias();
                sqlBuilder.append("(").append(sqlSelectParam.getSql()).append(")").append(columnAliasString).append(columnSeparator);
                parameterBinder.addParameterBinder(sqlSelectParam.getParameterBinder());
            }
        }
    }


    protected void parseFormTable(JoinTable joinTable) {
        if (joinTable instanceof FromJoin) {
            FromJoin fromJoin = (FromJoin) joinTable;
            Class<?> tableClass = fromJoin.getTableClass();
            TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
            String tableAlias = SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableAlias());
            sqlBuilder.append(getSchemaName(tableMeta)).append(syntaxAs()).append(tableAlias);
        }
        // INNER, LEFT, RIGHT, FULL, CROSS, SELF;
        if (joinTable instanceof InnerJoin) {
            InnerJoin innerJoin = (InnerJoin) joinTable;
            Class<?> tableClass = innerJoin.getTableClass();
            TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
            String tableAlias = SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableAlias());
            sqlBuilder.append(" ").append(SqlUtils.getSyntaxInnerJoin(sqlDialect))
                    .append(" ").append(getSchemaName(tableMeta)).append(syntaxAs()).append(tableAlias);
            //拼接On条件
            Consumer<Condition> onCondition = innerJoin.getOnCondition();
            WhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, dataSourceName);
            onCondition.accept(whereCondition);
            parameterBinder.addParameterBinder(whereCondition.getParameterBinder());
            sqlBuilder.append(" on").append(whereCondition.getWhereConditionSyntax());
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
