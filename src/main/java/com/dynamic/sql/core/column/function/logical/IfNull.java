package com.dynamic.sql.core.column.function.logical;

import com.dynamic.sql.core.column.function.FunctionExpression;

/** IFNULL 条件表达式，值参数可为列、嵌套表达式或绑定常量。 */
public final class IfNull extends FunctionExpression {
    public IfNull(Object value, Object fallback) { super("IFNULL",value,fallback); }
}
