package com.dynamic.sql.core.column.function.scalar.string;

import com.dynamic.sql.core.column.function.*;

/** OCTET_LENGTH 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class OctetLength extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public OctetLength(Object... values) { super("OCTET_LENGTH",FunctionArity.check(values,1,1,1)); }
}
