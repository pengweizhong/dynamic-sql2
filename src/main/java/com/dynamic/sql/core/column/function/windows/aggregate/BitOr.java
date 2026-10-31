package com.dynamic.sql.core.column.function.windows.aggregate;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;

/** BIT_OR 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class BitOr extends FunctionExpression implements AggregateFunction {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public BitOr(Object... values) { super("BIT_OR",FunctionArity.check(values,1,1,1)); }
}
