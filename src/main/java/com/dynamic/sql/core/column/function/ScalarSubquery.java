package com.dynamic.sql.core.column.function;

import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.utils.SqlUtils;

/** 标量子查询表达式，沿用框架子查询构建器及其参数顺序。 */
final class ScalarSubquery extends ColumnFunctionDecorator {
    private final SqlStatementSelectWrapper query;
    ScalarSubquery(SelectDsl select) { query=SqlUtils.executeNestedSelect(select); }
    @Override public String render(RenderContext context) { return "(" + query.getRawSql() + ")"; }
    @Override public ParameterBinder parameterBinder() { return query.getParameterBinder(); }
}
