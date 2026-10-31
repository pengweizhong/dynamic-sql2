package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.scalar.conversion.Cast;
import com.dynamic.sql.core.column.function.scalar.conversion.CastTarget;

/** 参数先按字符传输，避免服务端预编译先验证 JSON 而破坏 JSON_VALID 返回零的语义。 */
public final class JsonValid extends FunctionExpression {
    public JsonValid(Object value) { super("JSON_VALID",new Cast(value,CastTarget.CHAR)); }
}
