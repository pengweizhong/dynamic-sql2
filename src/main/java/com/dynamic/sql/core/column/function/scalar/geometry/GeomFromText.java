package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.utils.SqlUtils;

/** 通过绑定参数构造 WKT 几何，避免把业务文本直接拼接到 SQL。 */
public class GeomFromText extends ColumnFunctionDecorator implements ScalarFunction {
    private final String placeholder;
    private final int srid;

    /** 未指定坐标系时使用 SRID 0。 */
    public GeomFromText(String wkt) { this(wkt, 0); }

    /** NULL WKT 交由数据库按空间函数的 NULL 语义处理。 */
    public GeomFromText(String wkt, int srid) {
        this.placeholder = SqlUtils.registerValueWithKey(parameterBinder, wkt);
        this.srid = srid;
    }

    @Override public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException("ST_GeomFromText", context.getSqlDialect());
        return "ST_GeomFromText(" + placeholder + ", " + srid + ")";
    }
}
