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

import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.Value;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.json.JsonExtract;
import com.dynamic.sql.core.column.function.json.JsonUnquote;
import com.dynamic.sql.core.column.function.logical.Coalesce;
import com.dynamic.sql.core.column.function.logical.IfNull;
import com.dynamic.sql.core.column.function.scalar.number.Power;
import com.dynamic.sql.core.column.function.scalar.number.Sqrt;
import com.dynamic.sql.core.column.function.scalar.string.*;
import com.dynamic.sql.core.column.function.windows.aggregate.Count;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/** 不连接数据库，验证跨函数类型交叉嵌套的 SQL 结构及绑定契约。 */
class NestedFunctionExpressionTest {
    private PreparedSql prepared(AbstractColumFunction expression) {
        RenderContext context = new RenderContext();
        context.setSqlDialect(SqlDialect.MYSQL);
        return SqlUtils.parsePreparedObject(new StringBuilder(expression.render(context)), expression.parameterBinder());
    }

    private void assertStable(AbstractColumFunction expression, String expectedSql, Object... parameters) {
        for (int i = 0; i < 3; i++) {
            PreparedSql result = prepared(expression);
            assertEquals(expectedSql, result.getSql());
            assertEquals(Arrays.asList(parameters), result.getParams());
        }
    }

    @Test void stringsWrapLogicalFunctionsWhichWrapStrings() {
        assertStable(new Lower(new IfNull(new Trim(new Column("u", "name")), new Concat("guest", "!"))),
                "LOWER(IFNULL(TRIM(`u`.`name`), CONCAT(?, ?)))", "guest", "!");
    }

    @Test void logicalFunctionsWrapStringsWhichWrapLogicalFunctions() {
        assertStable(new Coalesce(new Lower(new IfNull(new Column("u", "name"), "UNKNOWN")),
                        new Trim(new Concat("fallback", " "))),
                "COALESCE(LOWER(IFNULL(`u`.`name`, ?)), TRIM(CONCAT(?, ?)))", "UNKNOWN", "fallback", " ");
    }

    @Test void jsonAndStringsNestInBothDirections() {
        assertStable(new Lower(new JsonUnquote(new JsonExtract(new Concat("{", "}"), "$.name"))),
                "LOWER(JSON_UNQUOTE(JSON_EXTRACT(CONCAT(?, ?), ?)))", "{", "}", "$.name");
    }

    @Test void numericExpressionsSupplySubstringPositions() {
        assertStable(new SubString(new Lower(new Column("u", "name")),
                        new Power(new Value(2), 3), new Sqrt(new Power(new Value(4), 2))),
                "SUBSTRING(LOWER(`u`.`name`), POWER(?, ?), SQRT(POWER(?, ?)))", 2, 3, 4, 2);
    }

    @Test void legacyCountWrapsNewFunctions() {
        assertStable(new Count(new Lower(new IfNull(new Column("u", "name"), "empty"))),
                "count(LOWER(IFNULL(`u`.`name`, ?)))", "empty");
    }

    @Test void newFunctionsWrapLegacyCount() {
        assertStable(new IfNull(new Count(new Trim(new Column("u", "name"))), new Power(new Value(2), 3)),
                "IFNULL(count(TRIM(`u`.`name`)), POWER(?, ?))", 2, 3);
    }

    @Test void sharedNestedInstanceBindsEachOccurrenceInOrder() {
        Lower shared = new Lower(new Concat("same", "same"));
        assertStable(new Concat(shared, "middle", shared),
                "CONCAT(LOWER(CONCAT(?, ?)), ?, LOWER(CONCAT(?, ?)))", "same", "same", "middle", "same", "same");
    }

    @Test void nestedNullAndHostileTextRemainBoundValues() {
        String hostile = "' OR 1=1 --";
        assertStable(new Lower(new IfNull(new Value(null), new Trim(new Concat(hostile, "tail")))),
                "LOWER(IFNULL(?, TRIM(CONCAT(?, ?))))", null, hostile, "tail");
    }
}
