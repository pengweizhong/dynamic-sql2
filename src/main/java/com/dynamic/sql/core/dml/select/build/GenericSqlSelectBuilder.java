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


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.AbstractAliasHelper;
import com.dynamic.sql.core.column.ColumnModifiers;
import com.dynamic.sql.core.column.conventional.AllColumn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.conventional.NumberColumn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.column.function.windows.Over;
import com.dynamic.sql.core.column.function.windows.aggregate.Count;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.core.dml.select.UnionSelect;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;
import com.dynamic.sql.core.dml.select.build.column.ColumnWrapper;
import com.dynamic.sql.core.dml.select.build.column.NestedColumn;
import com.dynamic.sql.core.dml.select.build.column.StringColumn;
import com.dynamic.sql.core.dml.select.build.join.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.model.Arithmetic;
import com.dynamic.sql.model.Dual;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.MapUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.SqlUtils;
import com.dynamic.sql.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public class GenericSqlSelectBuilder extends SqlSelectBuilder {

    public GenericSqlSelectBuilder(SelectSpecification selectSpecification, Map<String, TableAliasMapping> aliasTableMap) {
        super(selectSpecification);
        if (MapUtils.isNotEmpty(aliasTableMap)) {
            aliasTableMap.forEach(super.aliasTableMap::putIfAbsent);
        }
    }

    protected void parseColumnFunction() {
        String selectStr = SqlUtils.getSyntaxSelect(sqlDialect).concat(" ");
        sqlBuilder.append(selectStr);
        for (int i = 0; i < selectSpecification.getColumFunctions().size(); i++) {//NOSONAR
            ColumnQuery columnQuery = selectSpecification.getColumFunctions().get(i);
            String columnSeparator = ", ";
            if (isIgnoreColumn(columnQuery)) {
                if (StringUtils.endsWith(sqlBuilder.toString(), ", ")) {
                    sqlBuilder.delete(sqlBuilder.length() - columnSeparator.length(), sqlBuilder.length());
                }
                continue;
            }
            //最前/后一个列后面不追加逗号
            if (selectSpecification.getColumFunctions().size() - 1 == i /*|| sqlBuilder.length() == selectStr.length()*/) {
                columnSeparator = "";
            }
//            sqlBuilder.append(columnSeparator);
            if (columnQuery instanceof ColumFunction) {
                ColumFunction columFunction = (ColumFunction) columnQuery;
                String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect, version, aliasTableMap));
                sqlBuilder.append(functionToString);
                continue;
            }
            if (columnQuery instanceof StringColumn) {
                StringColumn stringColumn = (StringColumn) columnQuery;
                sqlBuilder.append(" ").append(stringColumn.getSql()).append(" ");
                continue;
            }
            if (columnQuery instanceof ColumnWrapper) {
                ColumnWrapper columnWrapper = (ColumnWrapper) columnQuery;
                ColumFunction columFunction = columnWrapper.getColumFunction();
                //是否查询的全部列
                if (columFunction instanceof AllColumn) {
                    //除了第一个后续元素都要追加`,`
                    parseAllColumn((AllColumn) columFunction);
                    sqlBuilder.append(columnSeparator);
                    continue;
                }
                StringBuilder arithmeticSql = new StringBuilder();
                ParameterBinder arithmeticParameterBinder = null;
                if (columFunction instanceof AbstractColumFunction) {
                    AbstractColumFunction abstractColumFunction = (AbstractColumFunction) columFunction;
                    abstractColumFunction.setAliasTableMap(aliasTableMap);
                    abstractColumFunction.setDataSourceName(dataSourceName);
//                    abstractColumFunction.setSqlDialect(sqlDialect);
//                    abstractColumFunction.setVersion(version);
//                    AbstractColumFunction delegateFunction = abstractColumFunction.getDelegateFunction();
//                    while (delegateFunction != null) {
//                        //判断动态函数，如果有则运算
////                        if (delegateFunction.getColumFunctionArithmetic() != null) {
////                            String functionToString = delegateFunction.getColumFunctionArithmetic().getFunctionToString(sqlDialect, version);
////                            parameterBinder.addParameterBinder(delegateFunction.getColumFunctionArithmetic().getParameterBinder());
////                            arithmeticSql.append(delegateFunction.getArithmeticSql()).append(functionToString);
////                        } else {
////                            arithmeticSql.append(delegateFunction.getArithmeticSql());
////                            parameterBinder.addParameterBinder(delegateFunction.getArithmeticgetParameterBinder());
////                        }
//                        delegateFunction.setArithmetic(new Arithmetic(delegateFunction.getArithmeticSql(), delegateFunction.getArithmeticgetParameterBinder()));
//                        delegateFunction = delegateFunction.getDelegateFunction();
//                    }
                    Arithmetic arithmetic = abstractColumFunction.getArithmetic();
                    arithmeticSql.append(arithmetic.getArithmeticSql());
                    arithmeticParameterBinder = arithmetic.getArithmeticParameterBinder();
                    parameterBinder.addParameterBinder(arithmeticParameterBinder);
                }
                //数字列不需要关心别名问题
                if (columFunction instanceof NumberColumn || columFunction instanceof Count) {
                    sqlBuilder.append(columFunction.render(new RenderContext(dataSourceName, sqlDialect, version, aliasTableMap))).append(arithmeticSql);
                    parameterBinder.addParameterBinder(arithmeticParameterBinder);
                    String columnAlias = StringUtils.isEmpty(columnQuery.getAlias()) ? "" : syntaxAs() + SqlUtils.quoteIdentifier(sqlDialect, columnQuery.getAlias());
                    sqlBuilder.append(columnAlias).append(columnSeparator);
                    continue;
                }
                Fn<?, ?> fn = columFunction.originColumn();
                if (columFunction instanceof AbstractAliasHelper) {
                    sqlBuilder.append(columFunction.render(new RenderContext(dataSourceName, sqlDialect, version, aliasTableMap))).append(arithmeticSql);
                    parameterBinder.addParameterBinder(arithmeticParameterBinder);
                    if (StringUtils.isNotBlank(columnQuery.getAlias())) {
                        sqlBuilder.append(syntaxAs()).append(columnQuery.getAlias());
                    }
                    sqlBuilder.append(columnSeparator);
                    continue;
                }
                String tableAlias = columFunction.getTableAlias();
                if (tableAlias == null) {
                    tableAlias = aliasTableMap.get(ReflectUtils.getOriginalClassCanonicalName(fn)).getAlias();
                }
                columFunction.setTableAlias(tableAlias);
                String functionToString = columFunction.render(new RenderContext(dataSourceName, sqlDialect, version, aliasTableMap));
                //拼接别名，
                String columnAlias = columnQuery.getAlias();
                //如果用户未设置别名 ，那么应该将字段名设置为别名，方便排序
                if (StringUtils.isEmpty(columnAlias) && Objects.nonNull(columFunction.originColumn())) {
                    columnAlias = ReflectUtils.fnToFieldName(columFunction.originColumn());
                    //加入限定符，防止和关键字冲突 只对未指定的列名进行限定
                    //如果用户指定了别名 则认为用户已经处理好了冲突问题
                    columnAlias = SqlUtils.quoteIdentifier(sqlDialect, columnAlias);
                }
                columnAlias = StringUtils.isEmpty(columnAlias) ? "" : syntaxAs() + columnAlias;
                sqlBuilder.append(functionToString).append(arithmeticSql);
                //追加over
                Consumer<Over> overConsumer = columnWrapper.getOver();
                if (overConsumer != null) {
                    Over over = new Over();
                    //初始化over块
                    overConsumer.accept(over);
                    String parseOrderBy = parseOrderBy(over.getOrderByList(), null);
                    over.setOverClause(parseOrderBy);
                    sqlBuilder.append(" ").append(over.toOverString(sqlDialect));
                }
                sqlBuilder.append(columnAlias);
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
                parameterBinder.addParameterBinder(columFunction.parameterBinder());
                parameterBinder.addParameterBinder(arithmeticParameterBinder);


            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                SelectDsl nestedColumnReference = nestedColumn.getNestedColumnReference();
                SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedColumnReference);
                String columnAliasString = syntaxAs() + SqlUtils.quoteIdentifier(sqlDialect, columnQuery.getAlias());
                sqlBuilder.append("(").append(sqlStatementWrapper.getRawSql()).append(")").append(columnAliasString).append(columnSeparator);
                parameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
            }
        }
    }

    /***
     * 判断当前查询的字段是否在忽略列表中
     */
    protected boolean isIgnoreColumn(ColumnQuery columnQuery) {
        if (selectSpecification.getIgnoreColumFunctions().isEmpty()) {
            return false;
        }
        if (!(columnQuery instanceof ColumnWrapper)) {
            return false;
        }
        ColumnWrapper columnWrapper = (ColumnWrapper) columnQuery;
        if (!(columnWrapper.getColumFunction() instanceof Column)) {
            return false;
        }
        Column column = (Column) columnWrapper.getColumFunction();
        for (ColumnQuery columFunction : selectSpecification.getIgnoreColumFunctions()) {
            ColumnWrapper ignoreColumnWrapper = (ColumnWrapper) columFunction;
            Column ignoreColumn = (Column) ignoreColumnWrapper.getColumFunction();
            String ignoreClassName = ReflectUtils.getOriginalClassCanonicalName(ignoreColumn.originColumn());
            String className = ReflectUtils.getOriginalClassCanonicalName(column.originColumn());
            // 检查列函数是否与给定的函数匹配
            if (Objects.equals(ignoreClassName, className)) {
                String ignoreField = ReflectUtils.fnToFieldName(ignoreColumn.originColumn());
                String field = ReflectUtils.fnToFieldName(column.originColumn());
                if (Objects.equals(ignoreField, field)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isIgnoreColumn(String tableClasspath, ColumnMeta columnMeta) {
        if (selectSpecification.getIgnoreColumFunctions().isEmpty()) {
            return false;
        }
        for (ColumnQuery columFunction : selectSpecification.getIgnoreColumFunctions()) {
            ColumnWrapper ignoreColumnWrapper = (ColumnWrapper) columFunction;
            Column ignoreColumn = (Column) ignoreColumnWrapper.getColumFunction();
            String ignoreClassName = ReflectUtils.getOriginalClassCanonicalName(ignoreColumn.originColumn());
            if (Objects.equals(ignoreClassName, tableClasspath)) {
                String ignoreField = ReflectUtils.fnToFieldName(ignoreColumn.originColumn());
                if (Objects.equals(ignoreField, columnMeta.getField().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回库名+表名+别名的结果
     */
    protected String automaticallySelectAliases(JoinTable joinTable) {
        Class<?> tableClass = joinTable.getTableClass();
        TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
        //DUAL 虚拟表不需要限定符号  也不需要别名
        if (tableClass == Dual.class) {
            return tableMeta.getTableName();
        }
        if (tableMeta == null) {
            throw new DynamicSqlException("Cannot find table mapping for class: " + tableClass.getCanonicalName());
        }
        String alias = StringUtils.isEmpty(joinTable.getTableAlias()) ? tableMeta.getTableAlias() : joinTable.getTableAlias();
        String tableAlias = SqlUtils.quoteIdentifier(sqlDialect, alias);
        return getSchemaTableFullName(tableMeta).concat(syntaxAs()).concat(tableAlias);
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
        if (joinTable instanceof FromUnionJoin) {
            FromUnionJoin fromUnionJoin = (FromUnionJoin) joinTable;
            UnionJoin unionJoin = fromUnionJoin.getUnionJoin();
            UnionSelect unionSelect = new UnionSelect(unionJoin.getUnionType());
            unionSelect.parseSelectDsls(unionJoin.getNestedSelects());
            sqlBuilder.append(" (").append(unionSelect.getRawSql()).append(") ")
                    .append(syntaxAs()).append(fromUnionJoin.getTableAlias());
            parameterBinder.addParameterBinder(unionSelect.getParameterBinder());
            return;
        }
        if (joinTable instanceof FromJoin) {
            //如果是直接查询的函数表
            if (joinTable.getTableFunction() != null) {
                TableFunction tableFunction = joinTable.getTableFunction().get();
                if (tableFunction instanceof Column) {
                    Column column = (Column) tableFunction;
                    column.setDataSourceName(dataSourceName);
                }
                sqlBuilder.append(tableFunction.render(new RenderContext(dataSourceName, sqlDialect, version, aliasTableMap)))
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
                    .append(syntaxAs()).append(SqlUtils.quoteIdentifier(sqlDialect, nestedJoin.getTableAlias()));
            parameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
            //拼接On条件
            appendOnCondition(joinTable);
            return;
        }
        if (joinTable instanceof TableFunctionJoin) {
            TableFunctionJoin tableFunctionJoin = (TableFunctionJoin) joinTable;
            TableFunction tableFunction = tableFunctionJoin.getTableFunction().get();
            String functionToString = tableFunction.render(new RenderContext(dataSourceName, sqlDialect, version, aliasTableMap));
            sqlBuilder.append(" ").append(syntaxJoin).append(functionToString).append(syntaxAs())
                    .append(SqlUtils.quoteIdentifier(sqlDialect, tableFunctionJoin.getTableAlias()));
            parameterBinder.addParameterBinder(tableFunction.parameterBinder());
            //拼接On条件
            appendOnCondition(joinTable);
            return;
        }
        if (joinTable instanceof UnionJoin) {
            UnionJoin unionJoin = (UnionJoin) joinTable;
            UnionSelect unionSelect = new UnionSelect(unionJoin.getUnionType());
            unionSelect.parseSelectDsls(unionJoin.getNestedSelects());
            sqlBuilder.append(" ").append(syntaxJoin).append(" (").append(unionSelect.getRawSql()).append(") ")
                    .append(syntaxAs()).append(SqlUtils.quoteIdentifier(sqlDialect, unionJoin.getTableAlias()));
            parameterBinder.addParameterBinder(unionSelect.getParameterBinder());
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
        Consumer<GenericWhereCondition> onCondition = joinTable.getOnCondition();
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

    public String parseLimit() {
        StringBuilder stringBuilder = new StringBuilder();
        LimitInfo limitInfo = selectSpecification.getLimitInfo();
        if (limitInfo == null) {
            return stringBuilder.toString();
        }
        stringBuilder.append(" ").append(SqlUtils.getSyntaxLimit(SqlDialect.MYSQL)).append(" ");
        if (limitInfo.getOffset() != null) {
            stringBuilder.append(registerValueWithKey(parameterBinder, limitInfo.getOffset())).append(", ");
        }
        stringBuilder.append(registerValueWithKey(parameterBinder, limitInfo.getLimit()));
        return stringBuilder.toString();
    }

    private void parseAllColumn(AllColumn allColumn) {
        //判断列查询的引用模式
        //别名引用
        String tableAlias = allColumn.getTableAlias();
        if (StringUtils.isNotEmpty(tableAlias)) {
            final String[] clazz = new String[1];
            aliasTableMap.forEach((cls, alias) -> {
                if (alias == null) {
                    return;
                }
                if (alias.equals(tableAlias)) {
                    clazz[0] = cls;
                }
            });
            if (clazz[0] == null || clazz[0].equals(tableAlias)) {
//                throw new IllegalArgumentException("Table alias does not exist: " + tableAlias);
//            }
//            //兼容内嵌
//            if (clazz[0].equals(tableAlias)) {
                sqlBuilder.append(tableAlias).append(".*");
                return;
            }
            List<ColumnMeta> columnMetas = TableProvider.getTableMeta(clazz[0]).getColumnMetas();
            appendQueryColumn(columnMetas, tableAlias, clazz[0]);
            return;
        }
        //类引用
        Class<?> tableClass = allColumn.getTableClass();
        if (tableClass != null) {
            appendQueryAllColumnForClass(tableClass);
            return;
        }
        JoinTable joinTable = this.selectSpecification.getJoinTables().get(0);
        if (joinTable instanceof UnionJoin) {
            if (tableAlias != null) {
                sqlBuilder.append(tableAlias).append(".");
            }
            sqlBuilder.append("*");
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
        //通常子查询可能无法正确获取别名
        if (aliasTableMap.get(canonicalName) == null) {
            sqlBuilder.append("*");
            return;
        }
        String tableAlias = aliasTableMap.get(canonicalName).getAlias();
        //确定别名
        tableAlias = tableAlias == null ? tableMeta.getTableAlias() : tableAlias;
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        appendQueryColumn(columnMetas, tableAlias, canonicalName);
    }

    private void appendQueryColumn(List<ColumnMeta> columnMetas, String tableAlias, String tableClasspath) {
        for (int i = 0; i < columnMetas.size(); i++) {
            ColumnMeta columnMeta = columnMetas.get(i);
            if (isIgnoreColumn(tableClasspath, columnMeta)) {
                continue;
            }
            //拼接别名，
            sqlBuilder.append(SqlUtils.quoteIdentifier(sqlDialect, tableAlias))
                    .append(".").append(SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getColumnName()))
                    .append(" ").append(SqlUtils.getSyntaxAs(sqlDialect)).append(" ")
                    .append(SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getField().getName()));
            if (columnMetas.size() - 1 > i) {
                sqlBuilder.append(", ");
            }
        }
    }
}
