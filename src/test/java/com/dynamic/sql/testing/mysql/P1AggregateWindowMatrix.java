package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.Value;
import com.dynamic.sql.core.column.function.scalar.conversion.Cast;
import com.dynamic.sql.core.column.function.scalar.conversion.CastTarget;
import com.dynamic.sql.core.column.function.scalar.geometry.AsText;
import com.dynamic.sql.core.column.function.string.GroupConcat;
import com.dynamic.sql.core.column.function.windows.*;
import com.dynamic.sql.core.column.function.windows.aggregate.*;
import com.dynamic.sql.enums.SortOrder;
import com.dynamic.sql.testing.model.Account;
import com.dynamic.sql.testing.model.MysqlSpatialSample;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DynamicTest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/** 聚合与窗口逐项核对独立 SQL，使用唯一主键保证行顺序。 */
final class P1AggregateWindowMatrix {
    private P1AggregateWindowMatrix() { }
    static Stream<DynamicTest> cases(SqlContext context,Connection connection) {
        List<DynamicTest> tests=new ArrayList<>();
        Column balance=new Column(Account::getBalance),id=new Column(Account::getId);
        aggregate(tests,context,connection,"StdDevPop",new StdDevPop(balance),"STDDEV_POP(balance)");
        aggregate(tests,context,connection,"StdDevSamp",new StdDevSamp(balance),"STDDEV_SAMP(balance)");
        aggregate(tests,context,connection,"VarPop",new VarPop(balance),"VAR_POP(balance)");
        aggregate(tests,context,connection,"VarSamp",new VarSamp(balance),"VAR_SAMP(balance)");
        aggregate(tests,context,connection,"BitAnd",new BitAnd(id),"BIT_AND(id)");
        aggregate(tests,context,connection,"BitOr",new BitOr(id),"BIT_OR(id)");
        aggregate(tests,context,connection,"BitXor",new BitXor(id),"BIT_XOR(id)");
        aggregate(tests,context,connection,"CountDistinct",Count.distinct(new Column(Account::getDepartmentId),new Column(Account::getStatus)),
                "COUNT(DISTINCT department_id,status)");
        tests.add(DynamicTest.dynamicTest("p1.aggregate.GroupConcat",()->{
            GroupConcat expression=new GroupConcat(new GroupConcat.GroupConcatOptions(Account::getLoginName)
                    .distinct(Account::getLoginName).orderBy(Account::getId,SortOrder.DESC).separator("'中文"));
            assertEquals("no_department'中文carol'中文bob'中文alice",
                    context.select().column(expression).from(Account.class).fetch(String.class).toOne());
        }));
        tests.add(DynamicTest.dynamicTest("p1.aggregate.JsonArrayAgg",()->{
            String actual=context.select().column(new JsonArrayAgg(id)).from(Account.class).fetch(String.class).toOne();
            List<Long> values=new ArrayList<>();
            JsonParser.parseString(actual).getAsJsonArray().forEach(v->values.add(v.getAsLong()));
            values.sort(Long::compareTo);assertEquals(Arrays.asList(100L,101L,102L,103L),values);
        }));
        tests.add(DynamicTest.dynamicTest("p1.aggregate.JsonObjectAgg",()->{
            String actual=context.select().column(new JsonObjectAgg(new Column(Account::getLoginName),balance))
                    .from(Account.class).fetch(String.class).toOne();
            assertEquals(JsonParser.parseString("{\"alice\":100.2500,\"bob\":0,\"carol\":-10.5000,\"no_department\":0}"),
                    JsonParser.parseString(actual));
        }));
        tests.add(DynamicTest.dynamicTest("p1.aggregate.Collect",()->assertEquals("MULTIPOINT((10 20))",
                context.select().column(new AsText(new Collect(new Column(MysqlSpatialSample::getPointValue))))
                        .from(MysqlSpatialSample.class).fetch(String.class).toOne())));
        Consumer<Over> ordered=o->o.orderBy(Account::getId);
        window(tests,context,connection,"Lag",new Lag(id,1,-1),ordered,"LAG(id,1,-1) OVER(ORDER BY id)");
        window(tests,context,connection,"Lead",new Lead(id,1,-1),ordered,"LEAD(id,1,-1) OVER(ORDER BY id)");
        window(tests,context,connection,"FirstValue",new FirstValue(id),ordered,"FIRST_VALUE(id) OVER(ORDER BY id)");
        window(tests,context,connection,"LastValue",new LastValue(id),o->o.orderBy(Account::getId)
                .rowsBetween(Over.Bound.unboundedPreceding(),Over.Bound.unboundedFollowing()),
                "LAST_VALUE(id) OVER(ORDER BY id ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)");
        window(tests,context,connection,"NthValue",new NthValue(id,2),ordered,"NTH_VALUE(id,2) OVER(ORDER BY id)");
        window(tests,context,connection,"NTile",new NTile(2),ordered,"NTILE(2) OVER(ORDER BY id)");
        window(tests,context,connection,"CumeDist",new CumeDist(),ordered,"CUME_DIST() OVER(ORDER BY id)");
        window(tests,context,connection,"PercentRank",new PercentRank(),ordered,"PERCENT_RANK() OVER(ORDER BY id)");
        window(tests,context,connection,"SumPartitionRows",new Sum(Account::getBalance),
                o->o.partitionBy(Account::getDepartmentId).orderBy(Account::getId)
                        .rowsBetween(Over.Bound.unboundedPreceding(),Over.Bound.currentRow()),
                "SUM(balance) OVER(PARTITION BY department_id ORDER BY id ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)");
        window(tests,context,connection,"CountPartition",new Count(1),
                o->o.partitionBy(Account::getDepartmentId),"COUNT(1) OVER(PARTITION BY department_id)");
        window(tests,context,connection,"SumRange",new Sum(Account::getBalance),
                o->o.orderBy(Account::getId).rangeBetween(Over.Bound.preceding(1),Over.Bound.currentRow()),
                "SUM(balance) OVER(ORDER BY id RANGE BETWEEN 1 PRECEDING AND CURRENT ROW)");
        tests.add(DynamicTest.dynamicTest("p1.aggregate.statisticsEdges",()->{
            assertEquals(0.0,context.select().column(new StdDevPop(balance)).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,100L)).fetch(Double.class).toOne(),1e-10);
            assertNull(context.select().column(new StdDevSamp(balance)).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,100L)).fetch(Double.class).toOne());
            assertNull(context.select().column(new VarSamp(balance)).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,-1L)).fetch(Double.class).toOne());
        }));
        tests.add(DynamicTest.dynamicTest("p1.aggregate.emptySets",()->{
            assertNull(context.select().column(new JsonArrayAgg(id)).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,-1L)).fetch(String.class).toOne());
            assertEquals("0",context.select().column(new BitOr(id)).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,-1L)).fetch(String.class).toOne());
            assertEquals("18446744073709551615",context.select().column(new BitAnd(id)).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,-1L)).fetch(String.class).toOne());
        }));
        tests.add(DynamicTest.dynamicTest("p1.aggregate.jsonNullKey",()->assertThrows(RuntimeException.class,
                ()->context.select().column(new JsonObjectAgg(new Value(null),id))
                        .from(Account.class).fetch(String.class).toOne())));
        tests.add(DynamicTest.dynamicTest("p1.aggregate.jsonDuplicateKey",()->{
            String json=context.select().column(new JsonObjectAgg(new Value("key"),id)).from(Account.class).fetch(String.class).toOne();
            assertEquals(1,JsonParser.parseString(json).getAsJsonObject().size());
            assertTrue(Arrays.asList(100L,101L,102L,103L).contains(
                    JsonParser.parseString(json).getAsJsonObject().get("key").getAsLong()));
        }));
        aggregate(tests,context,connection,"Avg",new Avg(balance),"AVG(balance)");
        aggregate(tests,context,connection,"Min",new Min(balance),"MIN(balance)");
        aggregate(tests,context,connection,"Max",new Max(balance),"MAX(balance)");
        window(tests,context,connection,"RankTies",new Rank(),o->o.orderBy(Account::getBalance),
                "RANK() OVER(ORDER BY balance)");
        window(tests,context,connection,"DenseRankTies",new DenseRank(),o->o.orderBy(Account::getBalance),
                "DENSE_RANK() OVER(ORDER BY balance)");
        window(tests,context,connection,"NullableSum",new Sum(Account::getDepartmentId),
                o->o.partitionBy(Account::getDepartmentId),"SUM(department_id) OVER(PARTITION BY department_id)");
        tests.add(DynamicTest.dynamicTest("p1.aggregate.GroupConcatTruncation",()->{
            long previous;
            try(Statement statement=connection.createStatement();ResultSet result=statement.executeQuery("SELECT @@session.group_concat_max_len")) {
                assertTrue(result.next());previous=result.getLong(1);
            }
            try {
                try(Statement statement=connection.createStatement()) { statement.execute("SET SESSION group_concat_max_len=8"); }
                String result=context.select().column(new GroupConcat(Account::getLoginName)).from(Account.class).fetch(String.class).toOne();
                assertTrue(result.length()<=8);
                try(Statement statement=connection.createStatement();ResultSet warnings=statement.executeQuery("SHOW WARNINGS")) {
                    assertTrue(warnings.next());assertEquals(1260,warnings.getInt(2));
                }
            } finally {
                try(Statement statement=connection.createStatement()) { statement.execute("SET SESSION group_concat_max_len="+previous); }
            }
        }));
        return tests.stream();
    }
    /** 聚合精度使用同连接的独立 SQL 文本基准。 */
    private static void aggregate(List<DynamicTest> tests,SqlContext context,Connection connection,String name,
            AbstractColumFunction expression,String reference) {
        tests.add(DynamicTest.dynamicTest("p1.aggregate."+name,()->{
            String expected;
            try(Statement statement=connection.createStatement();ResultSet result=statement.executeQuery(
                    "SELECT CAST("+reference+" AS CHAR) FROM ds_account")) { assertTrue(result.next());expected=result.getString(1); }
            assertEquals(expected,context.select().column(new Cast(expression,CastTarget.CHAR)).from(Account.class).fetch(String.class).toOne());
        }));
    }
    /** 不对窗口函数外层加 CAST，确保 OVER 紧随窗口函数并逐行比较含 NULL 结果。 */
    private static void window(List<DynamicTest> tests,SqlContext context,Connection connection,String name,
            AbstractColumFunction expression,Consumer<Over> over,String reference) {
        tests.add(DynamicTest.dynamicTest("p1.window."+name,()->{
            List<String> expected=new ArrayList<>();
            try(Statement statement=connection.createStatement();ResultSet result=statement.executeQuery(
                    "SELECT "+reference+" FROM ds_account ORDER BY id")) { while(result.next()) expected.add(result.getString(1)); }
            for(int i=0;i<2;i++) {
                List<String> actual=context.select().column((WindowsFunction)expression,over,"value")
                        .from(Account.class).orderBy(Account::getId).fetch(String.class).toList();
                assertEquals(expected,actual);
            }
        }));
    }
}
