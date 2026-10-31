package com.dynamic.sql.testing.unit;

import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.scalar.geometry.*;
import com.dynamic.sql.enums.SqlDialect;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/** 无需数据库的空间表达式契约，覆盖常量绑定、嵌套参数顺序和重复渲染稳定性。 */
class SpatialFunctionTest {
    /** GeoJSON 文本不能直接拼入 SQL，重复渲染不得增加绑定条目。 */
    @Test void bindsHostileJsonWithoutChangingSqlStructure() {
        RenderContext context = new RenderContext(); context.setSqlDialect(SqlDialect.MYSQL);
        String hostile = "' OR 1=1 --";
        GeomFromGeoJSON expression = new GeomFromGeoJSON(hostile,1,0);
        String sql = expression.render(context);
        assertFalse(sql.contains(hostile));
        assertEquals(Arrays.asList(hostile,1,0),new ArrayList<>(expression.parameterBinder().getValues()));
        assertEquals(sql,expression.render(context));
        assertEquals(3,expression.parameterBinder().getValues().size());
    }

    /** 参数顺序必须与左右几何表达式在 SQL 中的顺序一致。 */
    @Test void preservesNestedOperandOrder() {
        Intersection expression = new Intersection(new GeomFromText("POINT(1 2)"),new GeomFromText("POINT(3 4)"));
        assertEquals(Arrays.asList("POINT(1 2)","POINT(3 4)"),new ArrayList<>(expression.parameterBinder().getValues()));
        assertEquals(2,expression.parameterBinder().getValues().size());
    }

    /** 未实现的数据库方言必须拒绝渲染，防止把 MySQL 语法静默发送到其他数据库。 */
    @Test void refusesUnsupportedDialect() {
        RenderContext context = new RenderContext(); context.setSqlDialect(SqlDialect.ORACLE);
        assertThrows(RuntimeException.class,() -> new Transform(new GeomFromText("POINT(1 2)"),4326).render(context));
    }
}
