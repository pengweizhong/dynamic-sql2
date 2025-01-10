package com.dynamic.sql.core.dml.select.build;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.AbstractAliasHelper;
import com.dynamic.sql.core.column.ColumnModifiers;
import com.dynamic.sql.core.column.conventional.AllColumn;
import com.dynamic.sql.core.column.conventional.NumberColumn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.column.function.windows.Over;
import com.dynamic.sql.core.column.function.windows.aggregate.Count;
import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;
import com.dynamic.sql.core.dml.select.build.column.FunctionColumn;
import com.dynamic.sql.core.dml.select.build.column.NestedColumn;
import com.dynamic.sql.core.dml.select.build.join.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.SqlUtils;
import com.dynamic.sql.utils.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public class GenericSqlSelectBuilder extends SqlSelectBuilder {

    public GenericSqlSelectBuilder(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    protected void parseColumnFunction() {
        sqlBuilder.append(SqlUtils.getSyntaxSelect(sqlDialect)).append(" ");
        for (int i = 0; i < selectSpecification.getColumFunctions().size(); i++) {//NOSONAR
            ColumnQuery columnQuery = selectSpecification.getColumFunctions().get(i);
            String columnSeparator = ", ";
            //最后一个列后面不追加逗号
            if (selectSpecification.getColumFunctions().size() - 1 == i) {
                columnSeparator = "";
            }
            //TODO 这里类名需要修改以下，太容易引起混淆 ColumFunction、FunctionColumn
            if (columnQuery instanceof ColumFunction) {
                ColumFunction columFunction = (ColumFunction) columnQuery;
                String functionToString = columFunction.getFunctionToString(sqlDialect, version);
                sqlBuilder.append(functionToString);
                continue;
            }
            if (columnQuery instanceof FunctionColumn) {
                FunctionColumn functionColumn = (FunctionColumn) columnQuery;
                ColumFunction columFunction = functionColumn.getColumFunction();
                //是否查询的全部列
                if (columFunction instanceof AllColumn) {
                    //除了第一个后续元素都要追加`,`
                    parseAllColumn((AllColumn) columFunction);
                    sqlBuilder.append(columnSeparator);
                    continue;
                }
                StringBuilder arithmeticSql;
                ParameterBinder arithmeticParameterBinder = null;
                if (columFunction instanceof AbstractColumFunction) {
                    AbstractColumFunction abstractColumFunction = (AbstractColumFunction) columFunction;
                    abstractColumFunction.setAliasTableMap(aliasTableMap);
                    abstractColumFunction.setDataSourceName(dataSourceName);
                    arithmeticSql = abstractColumFunction.getArithmeticSql();
                    arithmeticParameterBinder = abstractColumFunction.getArithmeticParameterBinder();
                } else {
                    arithmeticSql = new StringBuilder();
                }
                //数字列不需要关心别名问题
                if (columFunction instanceof NumberColumn || columFunction instanceof Count) {
                    sqlBuilder.append(columFunction.getFunctionToString(sqlDialect, version)).append(arithmeticSql);
                    parameterBinder.addParameterBinder(arithmeticParameterBinder);
                    String columnAlias = StringUtils.isEmpty(columnQuery.getAlias()) ? "" : syntaxAs() + columnQuery.getAlias();
                    sqlBuilder.append(syntaxColumnAlias(columnAlias)).append(columnSeparator);
                    continue;
                }
                Fn<?, ?> fn = columFunction.getOriginColumnFn();
                if (columFunction instanceof AbstractAliasHelper) {
                    sqlBuilder.append(columFunction.getFunctionToString(sqlDialect, version)).append(arithmeticSql);
                    parameterBinder.addParameterBinder(arithmeticParameterBinder);
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
                sqlBuilder.append(functionToString).append(arithmeticSql);
                //追加over
                Consumer<Over> overConsumer = functionColumn.getOver();
                if (overConsumer != null) {
                    Over over = new Over();
                    //初始化over块
                    overConsumer.accept(over);
                    String parseOrderBy = parseOrderBy(over.getOrderByList());
                    over.setOverClause(parseOrderBy);
                    sqlBuilder.append(" ").append(over.toOverString(sqlDialect));
                }
                sqlBuilder.append(syntaxColumnAlias(columnAlias));
                //判断是否需要追加分隔逗号
                if (columFunction instanceof ColumnModifiers) {
                    ColumnModifiers columnModifiers = (ColumnModifiers) columFunction;
                    if (columnModifiers.shouldAppendDelimiter()) {
                        sqlBuilder.append(columnSeparator);
                    } else {
                        sqlBuilder.append(" ");
                    }
                } else {
                    sqlBuilder.append(columnSeparator);
                }
                parameterBinder.addParameterBinder(columFunction.getParameterBinder());
                parameterBinder.addParameterBinder(arithmeticParameterBinder);


            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                Consumer<AbstractColumnReference> nestedColumnReference = nestedColumn.getNestedColumnReference();
                SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedColumnReference);
                String columnAliasString = syntaxAs() + syntaxColumnAlias(columnQuery.getAlias());
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
            //如果是直接查询的函数表
            if (joinTable.getTableFunction() != null) {
                TableFunction tableFunction = joinTable.getTableFunction().get();
                sqlBuilder.append(tableFunction.getFunctionToString(sqlDialect, version))
                        .append(syntaxAs()).append(joinTable.getTableAlias());
                return;
            }
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

    private void parseAllColumn(AllColumn allColumn) {
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
        TableMeta tableMeta;
        try {
            tableMeta = TableProvider.getTableMeta(canonicalName);
        } catch (RuntimeException e) {
            tableMeta = null;
//            if (e.getCause() instanceof ClassNotFoundException) {
//                throw new IllegalArgumentException("Alias not found: " + canonicalName);
//            }
//            throw e;
        }
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
                    .append(" ").append(SqlUtils.getSyntaxAs(sqlDialect)).append(" ")
                    .append(syntaxColumnAlias(columnMeta.getField().getName()));
            if (columnMetas.size() - 1 > i) {
                sqlBuilder.append(", ");
            }
        }
    }
}
