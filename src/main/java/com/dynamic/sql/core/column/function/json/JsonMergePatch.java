package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;

/** JSON_MERGE_PATCH 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonMergePatch extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonMergePatch(Object... values) { super("JSON_MERGE_PATCH",FunctionArity.check(values,2,1000,1)); }
}
