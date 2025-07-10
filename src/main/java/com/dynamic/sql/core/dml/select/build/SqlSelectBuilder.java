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


import com.dynamic.sql.context.SchemaContextHolder;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.HavingCondition;
import com.dynamic.sql.core.dml.select.NestedMeta;
import com.dynamic.sql.core.dml.select.build.join.FromNestedJoin;
import com.dynamic.sql.core.dml.select.build.join.JoinTable;
import com.dynamic.sql.core.dml.select.build.join.NestedJoin;
import com.dynamic.sql.core.dml.select.build.join.TableFunctionJoin;
import com.dynamic.sql.core.dml.select.order.NullsFirst;
import com.dynamic.sql.core.dml.select.order.NullsLast;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.datasource.DataSourceMeta;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.SqlUtils;
import com.dynamic.sql.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SqlSelectBuilder {
    protected final StringBuilder sqlBuilder = new StringBuilder();
    protected final SelectSpecification selectSpecification;
    protected final Version version;
    protected final SqlDialect sqlDialect;
    protected final String dataSourceName;
    protected final ParameterBinder parameterBinder = new ParameterBinder();
    //key是class路径 value是别名
    //如果是嵌套表，则key和value都是别名
    protected final Map<String, TableAliasMapping> aliasTableMap = new HashMap<>();

    protected <C extends WhereCondition<C>> SqlSelectBuilder(SelectSpecification selectSpecification) {
        this.selectSpecification = selectSpecification;
        NestedMeta nestedMeta = selectSpecification.getNestedMeta();
        //是否传递了嵌套查询的元数据？
        if (nestedMeta != null) {
            this.version = nestedMeta.getVersion();
            this.sqlDialect = nestedMeta.getSqlDialect();
            this.dataSourceName = nestedMeta.getDataSourceName();
            return;
        }
        SchemaProperties schemaProperties;
        JoinTable joinTable = selectSpecification.getJoinTables().get(0);
        if (joinTable instanceof FromNestedJoin) {
            FromNestedJoin fromNestedJoin = (FromNestedJoin) joinTable;
            SqlStatementSelectWrapper sqlStatementWrapper = fromNestedJoin.getNestedJoin().getSqlStatementWrapper();
            String dataSourceName1 = sqlStatementWrapper.getDataSourceName();
            schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName1);
        } else {
            Class<?> tableClass = joinTable.getTableClass();
            TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
            schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        }
        this.version = getVersion(schemaProperties);
        this.sqlDialect = schemaProperties.getSqlDialect();
        this.dataSourceName = schemaProperties.getDataSourceName();
    }

    protected abstract void parseColumnFunction();

    /**
     * 解析From
     *
     * @return 是否继续执行解析，true继续，false终止
     */
    protected abstract boolean parseFormTables();

    protected abstract void parseLimit();

    public final SqlStatementSelectWrapper build() {
        //step0 解析表别名
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        joinTables.forEach(joinTable -> {
            String key;
            if (joinTable instanceof FromNestedJoin || joinTable instanceof NestedJoin
                    || joinTable instanceof TableFunctionJoin || joinTable.getTableFunction() != null) {
                key = joinTable.getTableAlias();
            } else {
                key = joinTable.getTableClass().getCanonicalName();
            }
            TableAliasMapping alias = aliasTableMap.get(key);
//            if (alias != null && joinTable.getTableAlias() == null) {
//                throw new IllegalStateException("Repeatedly associated with the same table: " + key + ", When querying " +
//                        "the same table continuously at the current level, aliases should be used to distinguish them");
//            }
            //只添加第一次设置的 别名，作为当前回话的全局别名
            if (alias == null) {
                TableAliasMapping aliasMapping = new TableAliasMapping();
                aliasMapping.setAlias(joinTable.getTableAlias());
                aliasMapping.setIsNestedJoin(joinTable instanceof NestedJoin || joinTable instanceof FromNestedJoin);
                aliasTableMap.put(key, aliasMapping);
            }
        });
        //step1 解析查询的列
        parseColumnFunction();
        sqlBuilder.append(" ");
        //step2 解析查询的表
        sqlBuilder.append(SqlUtils.getSyntaxFrom(sqlDialect)).append(" ");
        if (parseFormTables()) {
            //继续解析SQL
            continueParsingSql();
        }
        //猜测可能需要fetch的对象，如果用户没有生命具体的对象，就把这个类作为返回对象
        JoinTable guessTheTarget = joinTables.stream().filter(joinTable -> joinTable.getTableClass() != null)
                .findFirst().orElse(null);
        Class<?> guessTheTargetClass = null;
        if (guessTheTarget != null) {
            guessTheTargetClass = guessTheTarget.getTableClass();
        }
        return new SqlStatementSelectWrapper(dataSourceName, sqlBuilder, parameterBinder, guessTheTargetClass);
    }

    /**
     * 将解析表后的操作都聚拢在这里
     */
    protected void continueParsingSql() {
        //step3 解析where条件
        if (selectSpecification.getWhereCondition() != null) {
            Consumer<? extends WhereCondition<? extends Condition>> whereCondition = selectSpecification.getWhereCondition();
            parseWhere((Consumer<GenericWhereCondition>) whereCondition);
        }
        //step4 解析group by
        if (CollectionUtils.isNotEmpty(selectSpecification.getGroupByObject().getGroupByList())) {
            parseGroupBy(selectSpecification.getGroupByObject().getGroupByList());
        }
        //step5 解析聚合函数
        if (selectSpecification.getHavingCondition() != null) {
            parseHaving(selectSpecification.getHavingCondition());
        }
        //step6 解析order by
        if (CollectionUtils.isNotEmpty(selectSpecification.getOrderBys())) {
            sqlBuilder.append(parseOrderBy(selectSpecification.getOrderBys()));
        }
        //step7 解析limit
        if (selectSpecification.getLimitInfo() != null) {
            parseLimit();
        }
    }

    private void parseGroupBy(List<Object> groupByFields) {
        //List<Fn<?, ?>> groupByFields
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxGroupBy(sqlDialect));
        for (int i = 0; i < groupByFields.size(); i++) {
            if (i == 0) {
                sqlBuilder.append(" ");
            }
            //Fn<?, ?> fn = groupByFields.get(i);
            Object group = groupByFields.get(i);
            if (group instanceof Fn) {
                sqlBuilder.append(SqlUtils.extractQualifiedAlias((Fn) group, aliasTableMap, dataSourceName));
            }
            if (group instanceof ColumFunction) {
                ColumFunction columFunction = (ColumFunction) group;
                sqlBuilder.append(columFunction.getFunctionToString(sqlDialect, version, aliasTableMap));
                ParameterBinder whereParameterBinder = columFunction.getParameterBinder();
                parameterBinder.addParameterBinder(whereParameterBinder);
            }
            if (i < groupByFields.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
    }

    private void parseHaving(Consumer<HavingCondition<GenericWhereCondition>> havingCondition) {
        GenericWhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, aliasTableMap, dataSourceName);
        havingCondition.accept(whereCondition);
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxHaving(sqlDialect))
                .append(" ").append(whereCondition.getWhereConditionSyntax());
        ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
        parameterBinder.addParameterBinder(whereParameterBinder);
    }

    protected String parseOrderBy(List<OrderBy> orderBys) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ").append(SqlUtils.getSyntaxOrderBy(sqlDialect));
        for (int i = 0; i < orderBys.size(); i++) {
            OrderBy orderBy = orderBys.get(i);
            String columnSeparator = "";
            //最后一个列后面不追加逗号
            if (orderBys.size() - 1 != i) {
                columnSeparator = ",";
            }
            String sort = orderBy.getSortOrder() != null ? orderBy.getSortOrder().toSqlString(sqlDialect) : "";
            String aliasOrderBy = SqlUtils.extractQualifiedAliasOrderBy(orderBy, aliasTableMap, dataSourceName, version, parameterBinder);
            stringBuilder.append(aliasOrderBy).append(" ");
            if (i < orderBys.size() - 1) {
                //获取下个排列
                OrderBy nextOrderBy = orderBys.get(i + 1);
                //如果每次都想让null排在前面
                if (nextOrderBy instanceof NullsFirst) {
                    stringBuilder.append(" is not null, ").append(aliasOrderBy).append(" ");
                    //跳过当前对象
                    i++;
                }
                //如果每次都想让null排在后面
                if (nextOrderBy instanceof NullsLast) {
                    stringBuilder.append(" is null, ").append(aliasOrderBy).append(" ");
                    //跳过当前对象
                    i++;
                }
            }
            //判断下个排列是否属于 nullsLast 或者 nullsFirst
            stringBuilder.append(sort);
            if (i < orderBys.size() - 1) {
                stringBuilder.append(columnSeparator);
            }
        }
        return stringBuilder.toString();
    }

    private void parseWhere(Consumer<GenericWhereCondition> whereConditionConsumer) {
        GenericWhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, aliasTableMap, dataSourceName);
        whereConditionConsumer.accept(whereCondition);
        String whereConditionSyntax = whereCondition.getWhereConditionSyntax();
        if (StringUtils.isNotEmpty(whereConditionSyntax) && !whereConditionSyntax.trim().startsWith("limit")) {
            sqlBuilder.append(" ").append(SqlUtils.getSyntaxWhere(sqlDialect));
        }
        sqlBuilder.append(" ").append(whereConditionSyntax);
        ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
        parameterBinder.addParameterBinder(whereParameterBinder);
    }


//    private Version getVersion(Class<?> fromTableClass) {
//        TableMeta tableMeta = TableProvider.getTableMeta(fromTableClass);
//        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
//        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
//            DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
//            return new Version(dataSourceMeta.getMajorVersionNumber(),
//                    dataSourceMeta.getMinorVersionNumber(), dataSourceMeta.getPatchVersionNumber());
//        } else {
//            return new Version(schemaProperties.getMajorVersionNumber(),
//                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
//        }
//    }

    private Version getVersion(SchemaProperties schemaProperties) {
//        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
//            DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(schemaProperties.getDataSourceName());
//            return new Version(dataSourceMeta.getMajorVersionNumber(),
//                    dataSourceMeta.getMinorVersionNumber(), dataSourceMeta.getPatchVersionNumber());
//        } else {
        return new Version(schemaProperties.getMajorVersionNumber(),
                schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
//        }
    }


    protected String getSchemaName(TableMeta tableMeta) {
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        if (schemaProperties.isUseSchemaInQuery()) {
            DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
            return SqlUtils.quoteIdentifier(sqlDialect, dataSourceMeta.getSchema()) + "." +
                    SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableName());
        }
        return SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableName());
    }

    protected String syntaxAs() {
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        return schemaProperties.isUseAsInQuery() ? " " + SqlUtils.getSyntaxAs(sqlDialect) + " " : " ";
    }

    protected String syntaxColumnAlias(String columnAlias) {
        //Oracle要加限定名  oracle默认模式会将查询结果自动转为大写，导致无法映射字段
        if (sqlDialect == SqlDialect.ORACLE) {
            return "\"" + columnAlias + "\"";
        }
        return columnAlias;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }
}
