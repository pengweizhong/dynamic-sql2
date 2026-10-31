package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;

/** JSON_CONTAINS_PATH 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonContainsPath extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonContainsPath(Object... values) { super("JSON_CONTAINS_PATH",FunctionArity.check(values,3,1000,1)); }
}
