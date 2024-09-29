package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public abstract class SqlSelectBuilder {
    protected final StringBuilder sqlBuilder = new StringBuilder();
    protected final SelectSpecification selectSpecification;
    protected final Version version;
    protected final SqlDialect sqlDialect;
    protected final String dataSourceName;
    protected final ParameterBinder parameterBinder = new ParameterBinder();

    protected SqlSelectBuilder(SelectSpecification selectSpecification) {
        FromJoin fromJoin = (FromJoin) selectSpecification.getJoinTables().get(0);
        this.selectSpecification = selectSpecification;
        this.version = getVersion(fromJoin.getTableClass());
        this.sqlDialect = SqlUtils.getSqlDialect(fromJoin.getTableClass());
        this.dataSourceName = TableProvider.getTableMeta(fromJoin.getTableClass()).getBindDataSourceName();
    }

    protected abstract void parseColumnFunction();

    protected abstract void parseFormTable(JoinTable joinTable);

    protected abstract void parseLimit();

    public final SqlSelectParam build() {
        //step1 解析查询的列
        parseColumnFunction();
        sqlBuilder.append(" ");
        //step2 解析查询的表
        sqlBuilder.append(SqlUtils.getSyntaxFrom(sqlDialect)).append(" ");
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        joinTables.forEach(this::parseFormTable);
        //step3 解析where条件
        if (selectSpecification.getWhereCondition() != null) {
            parseWhereCondition(selectSpecification.getWhereCondition());
        }
        //step4 解析group by
        if (CollectionUtils.isNotEmpty(selectSpecification.getGroupByFields())) {
            parseGroupBy(selectSpecification.getGroupByFields());
        }
        //step5 解析聚合函数
        if (selectSpecification.getHavingCondition() != null) {
            parseHaving(selectSpecification.getHavingCondition());
        }
        //解析limit
        if (selectSpecification.getLimitInfo() != null) {
            parseLimit();
        }
        return new SqlSelectParam(sqlBuilder, parameterBinder);
    }

    private void parseHaving(Consumer<HavingCondition> havingCondition) {
        WhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, dataSourceName);
        havingCondition.accept(whereCondition);
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxHaving(sqlDialect))
                .append(" ").append(whereCondition.getWhereConditionSyntax());
        ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
        parameterBinder.addParameterBinder(whereParameterBinder);
    }

    private void parseGroupBy(List<Fn<?, ?>> groupByFields) {
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxGroupBy(sqlDialect));
        for (Fn<?, ?> groupByField : groupByFields) {
            sqlBuilder.append(" ").append(SqlUtils.qualifiedAliasName(groupByField));
        }
    }

    private void parseWhereCondition(Consumer<WhereCondition> whereConditionConsumer) {
        WhereCondition whereCondition = SqlUtils.matchDialectCondition(sqlDialect, version, dataSourceName);
        whereConditionConsumer.accept(whereCondition);
        sqlBuilder.append(" ").append(SqlUtils.getSyntaxWhere(sqlDialect))
                .append(" ").append(whereCondition.getWhereConditionSyntax());
        ParameterBinder whereParameterBinder = whereCondition.getParameterBinder();
        parameterBinder.addParameterBinder(whereParameterBinder);
    }


    private Version getVersion(Class<?> fromTableClass) {
        TableMeta tableMeta = TableProvider.getTableMeta(fromTableClass);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
            DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
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

}
