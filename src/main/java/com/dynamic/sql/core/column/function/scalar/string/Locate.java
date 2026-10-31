package com.dynamic.sql.core.column.function.scalar.string;

import com.dynamic.sql.core.column.function.*;

/** LOCATE 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class Locate extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public Locate(Object... values) { super("LOCATE",FunctionArity.check(values,2,3,1)); }
}
