package com.dynamic.sql.core.column.function.scalar.number;

import com.dynamic.sql.core.column.function.*;

/** LOG 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class Log extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public Log(Object... values) { super("LOG",FunctionArity.check(values,1,2,1)); }
}
