/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.testing.unit;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.Value;
import com.dynamic.sql.core.column.function.scalar.geometry.*;
import com.dynamic.sql.enums.SqlDialect;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 无需数据库的空间表达式契约，覆盖常量绑定、嵌套参数顺序和重复渲染稳定性。
 */
class SpatialFunctionTest {
    static class ShapeRow {
        public Object getGeometry() {
            return null;
        }
    }

    @Test
    void aliasConstructorsRetainOriginAcrossSpatialNesting() {
        FieldFn<ShapeRow, Object> field = ShapeRow::getGeometry;
        AbstractColumFunction[] expressions = {
                new Transform("g", field, 3857), new Simplify("g", field, 0.5),
                new PointN("g", field, 1), new StartPoint("g", field),
                new NumPoints("g", field), new GeometryN("g", field, 1),
                new LineInterpolatePoint("g", field, 0.5), new Validate("g", field)
        };
        for (AbstractColumFunction expression : expressions) {
            assertEquals("g", expression.getTableAlias());
            assertSame(field, expression.originColumn());
        }
        RenderContext context = new RenderContext();
        context.setSqlDialect(SqlDialect.MYSQL);
        AbstractColumFunction nested = new PointN(new Transform(new Column("g", "shape"), 3857), 2);
        String first = nested.render(context);
        assertTrue(first.contains("`g`.`shape`"));
        assertEquals(Arrays.asList(3857, 2), new ArrayList<>(nested.parameterBinder().getValues()));
        assertEquals(first, nested.render(context));
        assertEquals(Arrays.asList(3857, 2), new ArrayList<>(nested.parameterBinder().getValues()));
    }

    @Test
    void optionalSpatialOperandsCanNestAndRepeat() {
        RenderContext context = new RenderContext();
        context.setSqlDialect(SqlDialect.MYSQL);
        Buffer expression = new Buffer(new GeomFromText("POINT(1 2)"), new Value(2),
                new BufferStrategy("point_circle", 8), new BufferStrategy("join_round", 8));
        String first = expression.render(context);
        assertEquals(Arrays.asList("POINT(1 2)", 2, "point_circle", 8.0, "join_round", 8.0),
                new ArrayList<>(expression.parameterBinder().getValues()));
        assertEquals(first, expression.render(context));
        assertEquals(6, expression.parameterBinder().getValues().size());
        GeoHash hash = new GeoHash(new GeomFromText("POINT(1 2)", 4326), new Value(6));
        assertEquals(Arrays.asList("POINT(1 2)", 6), new ArrayList<>(hash.parameterBinder().getValues()));
        assertNotNull(new Buffer(ShapeRow::getGeometry, 2).originColumn());
        assertEquals("g", new GeoHash("g", ShapeRow::getGeometry, 6).getTableAlias());
    }

    /**
     * GeoJSON 文本不能直接拼入 SQL，重复渲染不得增加绑定条目。
     */
    @Test
    void bindsHostileJsonWithoutChangingSqlStructure() {
        RenderContext context = new RenderContext();
        context.setSqlDialect(SqlDialect.MYSQL);
        String hostile = "' OR 1=1 --";
        GeomFromGeoJSON expression = new GeomFromGeoJSON(hostile, 1, 0);
        String sql = expression.render(context);
        assertFalse(sql.contains(hostile));
        assertEquals(Arrays.asList(hostile, 1, 0), new ArrayList<>(expression.parameterBinder().getValues()));
        assertEquals(sql, expression.render(context));
        assertEquals(3, expression.parameterBinder().getValues().size());
    }

    /**
     * 参数顺序必须与左右几何表达式在 SQL 中的顺序一致。
     */
    @Test
    void preservesNestedOperandOrder() {
        Intersection expression = new Intersection(new GeomFromText("POINT(1 2)"), new GeomFromText("POINT(3 4)"));
        assertEquals(Arrays.asList("POINT(1 2)", "POINT(3 4)"), new ArrayList<>(expression.parameterBinder().getValues()));
        assertEquals(2, expression.parameterBinder().getValues().size());
    }

    /**
     * 未实现的数据库方言必须拒绝渲染，防止把 MySQL 语法静默发送到其他数据库。
     */
    @Test
    void refusesUnsupportedDialect() {
        RenderContext context = new RenderContext();
        context.setSqlDialect(SqlDialect.ORACLE);
        assertThrows(RuntimeException.class, () -> new Transform(new GeomFromText("POINT(1 2)"), 4326).render(context));
    }
}
