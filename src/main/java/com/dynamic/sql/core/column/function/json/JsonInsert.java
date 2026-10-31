package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;

/** JSON_INSERT 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonInsert extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonInsert(Object... values) { super("JSON_INSERT",FunctionArity.check(values,3,1001,2)); }
}
