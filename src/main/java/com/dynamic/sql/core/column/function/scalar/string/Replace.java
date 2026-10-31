package com.dynamic.sql.core.column.function.scalar.string;

import com.dynamic.sql.core.column.function.*;

/** REPLACE 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class Replace extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public Replace(Object... values) { super("REPLACE",FunctionArity.check(values,3,3,1)); }
}
