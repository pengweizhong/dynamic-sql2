package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.core.column.conventional.AllColumn;
import com.pengwz.dynamic.sql2.core.column.conventional.NumberColumn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.TableFunction;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Count;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.GenericWhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.NestedColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.*;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class GenericSqlSelectBuilder extends SqlSelectBuilder {

    public GenericSqlSelectBuilder(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    protected void parseColumnFunction() {
        sqlBuilder.append(SqlUtils.getSyntaxSelect(sqlDialect)).append(" ");
        for (int i = 0; i < selectSpecification.getColumFunctions().size(); i++) {
            ColumnQuery columnQuery = selectSpecification.getColumFunctions().get(i);
            String columnSeparator = "";
            //最后一个列后面不追加逗号
            if (selectSpecification.getColumFunctions().size() - 1 != i) {
                columnSeparator = ", ";
            }
            if (columnQuery instanceof FunctionColumn) {
                FunctionColumn functionColumn = (FunctionColumn) columnQuery;
                ColumFunction columFunction = functionColumn.getColumFunction();
                //是否查询的全部列
                if (columFunction instanceof AllColumn) {
                    //除了第一个后续元素都要追加`,`
                    if (i != 0) {
                        sqlBuilder.append(", ");
                    }
                    parseAllColumn((AllColumn) columFunction, selectSpecification.getColumFunctions().size() - 1 > i);
                    continue;
                }
                //数字列不需要关心别名问题
                if (columFunction instanceof NumberColumn || columFunction instanceof Count) {
                    sqlBuilder.append(columFunction.getFunctionToString(sqlDialect, version));
                    String columnAlias = StringUtils.isEmpty(columnQuery.getAlias()) ? "" : syntaxAs() + columnQuery.getAlias();
                    sqlBuilder.append(columnAlias).append(columnSeparator);
                    continue;
                }
                Fn<?, ?> fn = columFunction.getOriginColumnFn();
                if (columFunction instanceof AbstractAliasHelper) {
                    sqlBuilder.append(columFunction.getFunctionToString(sqlDialect, version));
                    if (StringUtils.isNotBlank(columnQuery.getAlias())) {
                        sqlBuilder.append(syntaxAs()).append(columnQuery.getAlias());
                    }
                    sqlBuilder.append(columnSeparator);
                    continue;
                }
                String tableAlias = columFunction.getTableAlias();
                if (tableAlias == null) {
                    tableAlias = aliasTableMap.get(ReflectUtils.getOriginalClassCanonicalName(fn));
                }
                columFunction.setTableAlias(tableAlias);
                String functionToString = columFunction.getFunctionToString(sqlDialect, version);
                //拼接别名，
                String columnAlias = StringUtils.isEmpty(columnQuery.getAlias()) ? "" : syntaxAs() + columnQuery.getAlias();
                sqlBuilder.append(functionToString).append(columnAlias).append(columnSeparator);
                parameterBinder.addParameterBinder(columFunction.getParameterBinder());
            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                Consumer<AbstractColumnReference> nestedColumnReference = nestedColumn.getNestedColumnReference();
                SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedColumnReference);
                String columnAliasString = syntaxAs() + columnQuery.getAlias();
                sqlBuilder.append("(").append(sqlStatementWrapper.getRawSql()).append(")").append(columnAliasString).append(columnSeparator);
                parameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
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
    protected boolean parseFormTables() {
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        for (JoinTable joinTable : joinTables) {
            parseJoinTable(joinTable);
        }
        return true;
    }

    protected void parseJoinTable(JoinTable joinTable) {
        if (joinTable instanceof FromNestedJoin) {
            FromNestedJoin fromNestedJoin = (FromNestedJoin) joinTable;
            NestedJoin nestedJoin = fromNestedJoin.getNestedJoin();
            SqlStatementSelectWrapper sqlStatementWrapper = parseNestedJoinSqlStatementWrapper(nestedJoin);
            sqlBuilder.append(" (").append(sqlStatementWrapper.getRawSql()).append(") ")
                    .append(syntaxAs()).append(nestedJoin.getTableAlias());
            parameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
            return;
        }
        if (joinTable instanceof FromJoin) {
            sqlBuilder.append(automaticallySelectAliases(joinTable));
            return;
        }
        String syntaxJoin = " " + SqlUtils.getSyntaxJoin(sqlDialect, joinTable.getJoinTableType()) + " ";
        if (joinTable instanceof NestedJoin) {
            NestedJoin nestedJoin = (NestedJoin) joinTable;
            SqlStatementSelectWrapper sqlStatementWrapper = parseNestedJoinSqlStatementWrapper(nestedJoin);
            sqlBuilder.append(" ").append(syntaxJoin).append(" (").append(sqlStatementWrapper.getRawSql()).append(") ")
                    .append(syntaxAs()).append(nestedJoin.getTableAlias());
            parameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
            //拼接On条件
            appendOnCondition(joinTable);
            return;
        }
        if (joinTable instanceof TableFunctionJoin) {
            TableFunctionJoin tableFunctionJoin = (TableFunctionJoin) joinTable;
            TableFunction tableFunction = tableFunctionJoin.getTableFunction().get();
            String functionToString = tableFunction.getFunctionToString(sqlDialect, version);
            sqlBuilder.append(" ").append(syntaxJoin).append(functionToString).append(syntaxAs()).append(tableFunctionJoin.getTableAlias());
            parameterBinder.addParameterBinder(tableFunction.getParameterBinder());
            //拼接On条件
            appendOnCondition(joinTable);
            return;
        }
        // INNER, LEFT, RIGHT, FULL, CROSS, SELF;
        sqlBuilder.append(syntaxJoin).append(automaticallySelectAliases(joinTable));
        //拼接On条件
        appendOnCondition(joinTable);
    }

    private void appendOnCondition(JoinTable joinTable) {
        Consumer<Condition> onCondition = joinTable.getOnCondition();
        if (onCondition != null) {
            String syntaxOn = " " + SqlUtils.getSyntaxOn(sqlDialect) + " ";
            GenericWhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, aliasTableMap, dataSourceName);
            onCondition.accept(whereCondition);
            parameterBinder.addParameterBinder(whereCondition.getParameterBinder());
            sqlBuilder.append(syntaxOn).append(whereCondition.getWhereConditionSyntax());
        }
    }

    private SqlStatementSelectWrapper parseNestedJoinSqlStatementWrapper(NestedJoin nestedJoin) {
        SqlStatementSelectWrapper sqlStatementWrapper = nestedJoin.getSqlStatementWrapper();
        if (sqlStatementWrapper == null) {
            sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedJoin.getNestedSelect());
        }
        return sqlStatementWrapper;
    }

    public void parseLimit() {
        LimitInfo limitInfo = selectSpecification.getLimitInfo();
        if (limitInfo == null) {
            return;
        }
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxLimit(SqlDialect.MYSQL)).append(" ");
        if (limitInfo.getOffset() != null) {
            sqlBuilder.append(registerValueWithKey(parameterBinder, limitInfo.getOffset())).append(", ");
        }
        sqlBuilder.append(registerValueWithKey(parameterBinder, limitInfo.getLimit()));
    }

    private void parseAllColumn(AllColumn allColumn, boolean isAppendComma) {
        //判断列查询的引用模式
        //别名引用
        String tableAlias = allColumn.getTableAlias();
        if (StringUtils.isNotEmpty(tableAlias)) {
            final String[] clazz = new String[1];
            String finalTableAlias = tableAlias;
            aliasTableMap.forEach((cls, alias) -> {
                if (alias == null) {
                    return;
                }
                if (alias.equals(finalTableAlias)) {
                    clazz[0] = cls;
                }
            });
            if (clazz[0] == null) {
                throw new IllegalArgumentException("Table alias does not exist: " + tableAlias);
            }
            //兼容内嵌
            if (clazz[0].equals(tableAlias)) {
                sqlBuilder.append(tableAlias).append(".*");
                if (isAppendComma) {
                    sqlBuilder.append(", ");
                }
                return;
            }
            List<ColumnMeta> columnMetas = TableProvider.getTableMeta(clazz[0]).getColumnMetas();
            appendQueryColumn(columnMetas, tableAlias);
            return;
        }
        //类引用
        Class<?> tableClass = allColumn.getTableClass();
        if (tableClass != null) {
            appendQueryAllColumnForClass(tableClass);
            return;
        }
        //都为空则表示匿名引用
        AtomicInteger cursor = new AtomicInteger();
        aliasTableMap.forEach((cls, alias) -> {
            if (cursor.getAndIncrement() != 0) {
                sqlBuilder.append(", ");
            }
            appendQueryAllColumnForClass(cls);
        });
    }

    private void appendQueryAllColumnForClass(Class<?> tableClass) {
        appendQueryAllColumnForClass(tableClass.getCanonicalName());
    }

    private void appendQueryAllColumnForClass(String canonicalName) {
        TableMeta tableMeta = TableProvider.getTableMeta(canonicalName);
        if (tableMeta == null) {
            sqlBuilder.append(canonicalName).append(".*");
            return;
        }
        String tableAlias = aliasTableMap.get(canonicalName);
        //确定别名
        tableAlias = tableAlias == null ? tableMeta.getTableAlias() : tableAlias;
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        appendQueryColumn(columnMetas, tableAlias);
    }

    private void appendQueryColumn(List<ColumnMeta> columnMetas, String tableAlias) {
        for (int i = 0; i < columnMetas.size(); i++) {
            ColumnMeta columnMeta = columnMetas.get(i);
            //拼接别名，
            sqlBuilder.append(SqlUtils.quoteIdentifier(sqlDialect, tableAlias))
                    .append(".").append(SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getColumnName()))
                    .append(" ").append(SqlUtils.getSyntaxAs(sqlDialect)).append(" ").append(columnMeta.getField().getName());
            if (columnMetas.size() - 1 > i) {
                sqlBuilder.append(", ");
            }
        }
    }
}
