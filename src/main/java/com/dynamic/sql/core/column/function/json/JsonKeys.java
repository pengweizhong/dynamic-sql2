package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;

/** JSON_KEYS 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonKeys extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonKeys(Object... values) { super("JSON_KEYS",FunctionArity.check(values,1,2,1)); }
}
