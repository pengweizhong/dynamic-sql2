package com.dynamic.sql.core.column.function.windows.aggregate;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;

/** JSON_OBJECTAGG 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonObjectAgg extends FunctionExpression implements AggregateFunction {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonObjectAgg(Object... values) { super("JSON_OBJECTAGG",FunctionArity.check(values,2,2,1)); }
}
