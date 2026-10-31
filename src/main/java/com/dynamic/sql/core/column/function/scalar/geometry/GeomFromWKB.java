package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.utils.SqlUtils;

/** 通过绑定参数构造 标准 WKB 几何，避免把业务文本直接拼接到 SQL。 */
public class GeomFromWKB extends ColumnFunctionDecorator implements ScalarFunction {
    private final String placeholder;
    private final int srid;

    /** 未指定坐标系时使用 SRID 0。 */
    public GeomFromWKB(byte[] wkb) { this(wkb, 0); }

    /** NULL 标准 WKB 交由数据库按空间函数的 NULL 语义处理。 */
    public GeomFromWKB(byte[] wkb, int srid) {
        this.placeholder = SqlUtils.registerValueWithKey(parameterBinder, wkb);
        this.srid = srid;
    }

    @Override public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException("ST_GeomFromWKB", context.getSqlDialect());
        return "ST_GeomFromWKB(" + placeholder + ", " + srid + ")";
    }
}
