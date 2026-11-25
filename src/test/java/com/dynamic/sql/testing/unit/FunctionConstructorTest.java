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
import com.dynamic.sql.core.column.function.ExpressionArguments;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.Value;
import com.dynamic.sql.core.column.function.scalar.datetime.*;
import com.dynamic.sql.core.column.function.scalar.number.Power;
import com.dynamic.sql.core.column.function.scalar.string.Concat;
import com.dynamic.sql.core.column.function.scalar.string.Lower;
import com.dynamic.sql.core.column.function.scalar.string.SubString;
import com.dynamic.sql.core.column.function.scalar.string.Trim;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FunctionConstructorTest {
    static class Row {
        public String getName() {
            return null;
        }

        public Long getId() {
            return null;
        }
    }

    private PreparedSql prepared(AbstractColumFunction expression) {
        RenderContext context = new RenderContext();
        context.setSqlDialect(SqlDialect.MYSQL);
        return SqlUtils.parsePreparedObject(new StringBuilder(expression.render(context)), expression.parameterBinder());
    }

    @Test
    void methodReferencesPreserveColumnOrigin() {
        FieldFn<Row, String> field = Row::getName;
        Lower direct = new Lower(field);
        assertSame(field, direct.originColumn());
        assertEquals("u", new Lower("u", field).getTableAlias());
        assertNotNull(new Lower(Row::getName).originColumn());
        assertNotNull(new Power(Row::getId, 2).originColumn());
        assertEquals("u", new Power("u", Row::getId, 2).getTableAlias());
    }

    @Test
    void nestingRetainsSqlAndBindingOrderAcrossRepeatedRendering() {
        AbstractColumFunction expression = new Lower(new Trim(new Concat(new Column("u", "name"), "suffix")));
        PreparedSql first = prepared(expression);
        assertEquals("LOWER(TRIM(CONCAT(`u`.`name`, ?)))", first.getSql());
        assertEquals(Arrays.asList("suffix"), first.getParams());
        assertEquals(first.getSql(), prepared(expression).getSql());
        assertEquals(first.getParams(), prepared(expression).getParams());
        assertEquals("u", expression.getTableAlias());
    }

    @Test
    void substringPositionsCanBeNestedExpressions() {
        PreparedSql sql = prepared(new SubString(new Column("u", "name"), new Value(1), new Power(new Value(2), 3)));
        assertEquals("SUBSTRING(`u`.`name`, ?, POWER(?, ?))", sql.getSql());
        assertEquals(Arrays.asList(1, 2, 3), sql.getParams());
    }

    @Test
    void explicitColumnsAndStringConstantsRemainDistinct() {
        assertEquals("LOWER(`u`.`name`)", prepared(new Lower("u", "name")).getSql());
        assertEquals(Arrays.asList("name"), prepared(new Lower("name")).getParams());
        assertEquals(Arrays.asList("left", "right"), prepared(new Concat("left", "right")).getParams());
    }

    @Test
    void dateMethodReferenceOverloadsPreserveSqlNull() {
        assertEquals(Arrays.asList(null, 1), prepared(new DateAdd(null, 1, DateTimeUnit.DAY)).getParams());
        assertEquals(Arrays.asList(null, 1), prepared(new DateSub(null, 1, DateTimeUnit.DAY)).getParams());
        assertEquals(Arrays.asList((Object) null), prepared(new Extract(DateTimeUnit.YEAR, null)).getParams());
        assertEquals(Arrays.asList(null, "2024-03-01"),
                prepared(new TimestampDiff(DateTimeUnit.DAY, null, "2024-03-01")).getParams());
    }

    @Test
    void specializedDateFunctionsPreserveOriginAndAlias() {
        FieldFn<Row, String> field = Row::getName;
        assertSame(field, new DateAdd(field, 1, DateTimeUnit.DAY).originColumn());
        assertEquals("u", new DateSub("u", field, 1, DateTimeUnit.DAY).getTableAlias());
        assertEquals("u", new Extract(DateTimeUnit.YEAR, "u", field).getTableAlias());
        assertEquals("u", new TimestampDiff(DateTimeUnit.DAY, "u", field, "2024-03-01").getTableAlias());
        AbstractColumFunction expression = new DateFormat(
                new DateAdd(new Column("u", "created_at"), new Value(2), DateTimeUnit.DAY), "%Y-%m-%d");
        PreparedSql sql = prepared(expression);
        assertEquals("DATE_FORMAT(DATE_ADD(`u`.`created_at`, INTERVAL ? DAY), ?)", sql.getSql());
        assertEquals(Arrays.asList(2, "%Y-%m-%d"), sql.getParams());
        assertEquals("u", expression.getTableAlias());
        assertEquals(sql.getParams(), prepared(expression).getParams());
    }

    @Test
    void fieldVariablesInAnyArgumentAreColumnsInsteadOfBoundValues() {
        FieldFn<Row, String> field = Row::getName;
        ExpressionArguments arguments = new ExpressionArguments();
        arguments.add(field);
        assertTrue(arguments.binder().getValues().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> new Power(Row::getId));
    }
}
