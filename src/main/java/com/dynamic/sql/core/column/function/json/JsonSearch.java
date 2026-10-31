package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;

/** JSON_SEARCH 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class JsonSearch extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public JsonSearch(Object... values) { super("JSON_SEARCH",FunctionArity.check(values,3,1000,1)); }
}
