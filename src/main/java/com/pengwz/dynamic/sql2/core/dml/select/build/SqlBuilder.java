package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.conventional.NumberColumn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.NestedColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class SqlBuilder {
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final SelectSpecification selectSpecification;
    private final Version version;
    private final SqlDialect sqlDialect;
    private final String dataSourceName;


    public SqlBuilder(SelectSpecification selectSpecification) {
        FromJoin fromJoin = (FromJoin) selectSpecification.getJoinTables().get(0);
        this.selectSpecification = selectSpecification;
        this.version = getVersion(fromJoin.getTableClass());
        this.sqlDialect = getSqlDialect(fromJoin.getTableClass());
        this.dataSourceName = TableProvider.getTableMeta(fromJoin.getTableClass()).getBindDataSourceName();
    }

    public StringBuilder build() {
        //解析查询的列
        parseColumnFunction();
        sqlBuilder.append(" ");
        //解析查询的表
        parseFormTable();
        System.out.println("SQL解析后的结果：\n" + sqlBuilder);
        return sqlBuilder;
    }


    private void parseColumnFunction() {
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
            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                Consumer<NestedSelect> nestedSelectConsumer = nestedColumn.getNestedSelect();
                NestedSelect nestedSelect = new NestedSelect();
                nestedSelectConsumer.accept(nestedSelect);
                StringBuilder nestedStringBuilder = new SqlBuilder(nestedSelect.getSelectSpecification()).build();
                System.out.println("测试内嵌列输出结果 ---> " + nestedStringBuilder);
                String columnAliasString = syntaxAs() + columnQuery.getAlias();
                sqlBuilder.append("(").append(nestedStringBuilder).append(")").append(columnAliasString).append(columnSeparator);
            }
        }
    }

    private void parseFormTable() {
        List<JoinTable> joinTables = selectSpecification.getJoinTables();
        sqlBuilder.append(SqlUtils.getSyntaxFrom(sqlDialect)).append(" ");
        //第一个一定是首次查询的表
        for (JoinTable joinTable : joinTables) {
            if (joinTable instanceof FromJoin) {
                FromJoin fromJoin = (FromJoin) joinTable;
                Class<?> tableClass = fromJoin.getTableClass();
                TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
                String tableName = SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableName());
                String tableAlias = SqlUtils.quoteIdentifier(sqlDialect, tableMeta.getTableAlias());
                sqlBuilder.append(tableName).append(syntaxAs()).append(tableAlias);
            }

        }
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

    private SqlDialect getSqlDialect(Class<?> fromTableClass) {
        TableMeta tableMeta = TableProvider.getTableMeta(fromTableClass);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        return schemaProperties.getSqlDialect();
    }

    private String syntaxAs() {
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
        return schemaProperties.isUseAsInQuery() ? " " + SqlUtils.getSyntaxAs(sqlDialect) + " " : " ";
    }


}
