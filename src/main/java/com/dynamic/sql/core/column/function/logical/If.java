package com.dynamic.sql.core.column.function.logical;

import com.dynamic.sql.core.column.function.FunctionExpression;

/** IF 条件表达式，值参数可为列、嵌套表达式或绑定常量。 */
public final class If extends FunctionExpression {
    public If(Object condition, Object whenTrue, Object whenFalse) { super("IF",condition,whenTrue,whenFalse); }
}
