package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.condition.impl.dialect.GenericWhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromNestedJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.NestedJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.TableFunctionJoin;
import com.pengwz.dynamic.sql2.core.dml.select.order.OrderBy;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

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
    protected final Map<String, String> aliasTableMap = new HashMap<>();

    protected SqlSelectBuilder(SelectSpecification selectSpecification) {
        this.selectSpecification = selectSpecification;
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
        this.version = matchVersion(schemaProperties);
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
            if (joinTable instanceof FromNestedJoin || joinTable instanceof NestedJoin || joinTable instanceof TableFunctionJoin) {
                key = joinTable.getTableAlias();
            } else {
                key = joinTable.getTableClass().getCanonicalName();
            }
            String alias = aliasTableMap.get(key);
            if (alias != null && joinTable.getTableAlias() == null) {
                throw new IllegalStateException("Repeatedly associated with the same table: " + key + ", When querying " +
                        "the same table continuously at the current level, aliases should be used to distinguish them");
            }
            //只添加第一次设置的 别名，作为当前回话的全局别名
            if (alias == null) {
                aliasTableMap.put(key, joinTable.getTableAlias());
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
            parseWhere(selectSpecification.getWhereCondition());
        }
        //step4 解析group by
        if (CollectionUtils.isNotEmpty(selectSpecification.getGroupByFields())) {
            parseGroupBy(selectSpecification.getGroupByFields());
        }
        //step5 解析聚合函数
        if (selectSpecification.getHavingCondition() != null) {
            parseHaving(selectSpecification.getHavingCondition());
        }
        //step6 解析order by
        if (CollectionUtils.isNotEmpty(selectSpecification.getOrderBys())) {
            parseOrderBy(selectSpecification.getOrderBys());
        }
        //step7 解析limit
        if (selectSpecification.getLimitInfo() != null) {
            parseLimit();
        }
    }

    private void parseGroupBy(List<Fn<?, ?>> groupByFields) {
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxGroupBy(sqlDialect));
        for (Fn<?, ?> groupByField : groupByFields) {
            sqlBuilder.append(" ").append(SqlUtils.extractQualifiedAlias(groupByField, aliasTableMap, dataSourceName));
        }
    }

    private void parseHaving(Consumer<HavingCondition> havingCondition) {
        GenericWhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, aliasTableMap, dataSourceName);
        havingCondition.accept(whereCondition);
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxHaving(sqlDialect))
                .append(" ").append(whereCondition.getWhereConditionSyntax());
        ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
        parameterBinder.addParameterBinder(whereParameterBinder);
    }

    private void parseOrderBy(List<OrderBy> orderBys) {
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxOrderBy(sqlDialect));
        for (int i = 0; i < orderBys.size(); i++) {
            OrderBy orderBy = orderBys.get(i);
            String columnSeparator = "";
            //最后一个列后面不追加逗号
            if (orderBys.size() - 1 != i) {
                columnSeparator = ",";
            }
            sqlBuilder.append(SqlUtils.extractQualifiedAliasOrderBy(orderBy, aliasTableMap, dataSourceName)).append(columnSeparator);
        }
    }

    private void parseWhere(Consumer<WhereCondition> whereConditionConsumer) {
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

    private Version matchVersion(SchemaProperties schemaProperties) {
        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
            DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(schemaProperties.getDataSourceName());
            return new Version(dataSourceMeta.getMajorVersionNumber(),
                    dataSourceMeta.getMinorVersionNumber(), dataSourceMeta.getPatchVersionNumber());
        } else {
            return new Version(schemaProperties.getMajorVersionNumber(),
                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
        }
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

    public String getDataSourceName() {
        return dataSourceName;
    }
}
