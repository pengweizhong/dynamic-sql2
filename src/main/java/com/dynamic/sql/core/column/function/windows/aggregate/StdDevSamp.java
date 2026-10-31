package com.dynamic.sql.core.column.function.windows.aggregate;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;

/** STDDEV_SAMP 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class StdDevSamp extends FunctionExpression implements AggregateFunction {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public StdDevSamp(Object... values) { super("STDDEV_SAMP",FunctionArity.check(values,1,1,1)); }
}
