package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;

public abstract class SqlBuilder {
    protected final StringBuilder sqlBuilder = new StringBuilder();
    protected final SelectSpecification selectSpecification;
    protected final Version version;
    protected final SqlDialect sqlDialect;
    protected final String dataSourceName;
    protected final ParameterBinder parameterBinder = new ParameterBinder();

    protected SqlBuilder(SelectSpecification selectSpecification) {
        FromJoin fromJoin = (FromJoin) selectSpecification.getJoinTables().get(0);
        this.selectSpecification = selectSpecification;
        this.version = getVersion(fromJoin.getTableClass());
        this.sqlDialect = getSqlDialect(fromJoin.getTableClass());
        this.dataSourceName = TableProvider.getTableMeta(fromJoin.getTableClass()).getBindDataSourceName();
    }

    protected abstract void parseColumnFunction();

    protected abstract void parseFormTable(JoinTable joinTable);

    public final StringBuilder build() {
        //step1 解析查询的列
        parseColumnFunction();
        sqlBuilder.append(" ");
        //step2 解析查询的表
        sqlBuilder.append(SqlUtils.getSyntaxFrom(sqlDialect)).append(" ");
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        joinTables.forEach(this::parseFormTable);
        //解析limit
        parseLimit();
        System.out.println("-- SQL解析后的结果：\n" + sqlBuilder);

        System.out.println("-- SQL解析后的结果+参数：\n" + parameterBinder.replacePlaceholdersWithValues(sqlBuilder.toString()));
        return sqlBuilder;
    }

    protected abstract void parseLimit();


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

    private SqlDialect getSqlDialect(Class<?> fromTableClass) {
        TableMeta tableMeta = TableProvider.getTableMeta(fromTableClass);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        return schemaProperties.getSqlDialect();
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


    protected ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

}
