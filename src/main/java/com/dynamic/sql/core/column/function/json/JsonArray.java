package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;

/** JSON_ARRAY 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonArray extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonArray(Object... values) { super("JSON_ARRAY",FunctionArity.check(values,0,1000,1)); }
}
