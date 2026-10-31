package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.Value;
import com.dynamic.sql.core.column.function.table.JsonTable;
import com.dynamic.sql.core.column.function.table.JsonTable.JsonColumn;
import com.dynamic.sql.testing.model.Account;
import org.junit.jupiter.api.DynamicTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/** JSON_TABLE 通过业务表关联执行，验证嵌套列、空值、默认值和严格错误策略。 */
final class P1JsonTableMatrix {
    private P1JsonTableMatrix() { }
    static Stream<DynamicTest> cases(SqlContext context) {
        List<DynamicTest> tests=new ArrayList<>();
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.array",()->{
            JsonTable table=new JsonTable(new Value("[1,2]"),"$[*]",
                    JsonColumn.builder().column("n").dataType("INT").jsonPath("$").build());
            assertEquals(Arrays.asList(1L,2L),values(context,table));
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.empty",()->{
            JsonTable table=new JsonTable(new Value("[]"),"$[*]",
                    JsonColumn.builder().column("n").dataType("INT").jsonPath("$").build());
            assertEquals(Collections.emptyList(),values(context,table));
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.nested",()->{
            JsonTable table=new JsonTable(new Value("[{\"items\":[1,2]},{\"items\":[3]}]"),"$[*]",
                    JsonColumn.nested("$.items[*]",JsonColumn.builder().column("n").dataType("INT").jsonPath("$").build()));
            assertEquals(Arrays.asList(1L,2L,3L),values(context,table));
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.ordinality",()->{
            JsonTable table=new JsonTable(new Value("[9,8]"),"$[*]",
                    JsonColumn.builder().column("n").ordinality().build());
            assertEquals(Arrays.asList(1L,2L),values(context,table));
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.nullOnError",()->{
            JsonTable table=new JsonTable(new Value("[{\"n\":{}},{}]"),"$[*]",
                    JsonColumn.builder().column("n").dataType("INT").jsonPath("$.n").on().error().build());
            List<Long> result=values(context,table);
            assertEquals(Arrays.asList(null,null),result);
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.errorOnEmpty",()->{
            JsonTable table=new JsonTable(new Value("[{}]"),"$[*]",
                    JsonColumn.builder().column("n").dataType("INT").jsonPath("$.n").errorOnEmpty().build());
            assertThrows(RuntimeException.class,()->values(context,table));
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.errorOnError",()->{
            JsonTable table=new JsonTable(new Value("[{\"n\":{}}]"),"$[*]",
                    JsonColumn.builder().column("n").dataType("INT").jsonPath("$.n").errorOnError().build());
            assertThrows(RuntimeException.class,()->values(context,table));
        }));
        tests.add(DynamicTest.dynamicTest("p1.jsonTable.defaultQuote",()->{
            JsonTable table=new JsonTable(new Value("[{}]"),"$[*]",
                    JsonColumn.builder().column("n").dataType("VARCHAR(32)").jsonPath("$.n")
                            .defaultValue("O'Reilly").build());
            assertEquals(Collections.singletonList("O'Reilly"),context.select().column("j","n").from(Account.class)
                    .join(()->table,"j").where(w->w.andEqualTo(Account::getId,100L)).fetch(String.class).toList());
        }));
        return tests.stream();
    }
    /** 固定一个账户消除关联乘法，只按派生列核对实际展开结果。 */
    private static List<Long> values(SqlContext context,JsonTable table) {
        return context.select().column("j","n").from(Account.class).join(()->table,"j")
                .where(w->w.andEqualTo(Account::getId,100L)).orderBy(new Column("j","n")).fetch(Long.class).toList();
    }
}
