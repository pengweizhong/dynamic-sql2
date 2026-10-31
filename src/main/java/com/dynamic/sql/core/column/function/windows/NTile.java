package com.dynamic.sql.core.column.function.windows;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.core.Version;

/** NTILE 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class NTile extends FunctionExpression implements WindowsFunction {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public NTile(Object... values) { super("NTILE",FunctionArity.check(values,1,1,1)); }
    @Override public String render(RenderContext context) {
        if(context.getVersion()!=null && context.getVersion().compareTo(new Version(8,0,0))<0)
            throw new UnsupportedOperationException("Window functions require MySQL 8.0 or later");
        return super.render(context);
    }
}
