package com.dynamic.sql.core.column.function.logical;

import com.dynamic.sql.core.column.function.FunctionExpression;

/** NULLIF 条件表达式，值参数可为列、嵌套表达式或绑定常量。 */
public final class NullIf extends FunctionExpression {
    public NullIf(Object left, Object right) { super("NULLIF",left,right); }
}
