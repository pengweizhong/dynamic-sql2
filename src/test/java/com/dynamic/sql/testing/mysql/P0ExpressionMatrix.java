package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.logical.*;
import com.dynamic.sql.core.column.function.scalar.conversion.*;
import com.dynamic.sql.core.column.function.json.*;
import com.dynamic.sql.core.column.function.scalar.string.SubString;
import com.dynamic.sql.core.column.function.scalar.datetime.DateFormat;
import com.dynamic.sql.core.column.function.windows.aggregate.Sum;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.testing.model.Account;
import com.dynamic.sql.testing.model.MysqlTypeSample;
import org.junit.jupiter.api.DynamicTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/** P0 表达式的实际 MySQL 语义与结果映射验证，常量和嵌套全部走参数绑定。 */
final class P0ExpressionMatrix {
    private P0ExpressionMatrix() { }
    static Stream<DynamicTest> cases(SqlContext context) {
        List<DynamicTest> tests=new ArrayList<>();
        number(tests,context,"add.expression",new Value(2).add(new Value(3)),5);
        number(tests,context,"subtract.expression",new Value(8).subtract(new Value(3)),5);
        number(tests,context,"multiply.expression",new Value(2).multiply(new Value(3)),6);
        number(tests,context,"divide.expression",new Value(12).divide(new Value(3)),4);
        number(tests,context,"precedence",new Value(2).add(new Value(3)).multiply(4),14);
        number(tests,context,"group",new Group(new Value(2).add(3)).multiply(4),20);
        number(tests,context,"column",new Column(Account::getBalance).add(2),102.25);
        number(tests,context,"fieldOperand",new Value(2).add(Account::getBalance),102.25);
        number(tests,context,"subquery",new Value(2).add((SelectDsl)q -> q.column(Account::getBalance)
                .from(Account.class).where(w->w.andEqualTo(Account::getId,101L))),2);
        text(tests,context,"coalesce",new Coalesce(null,new Value(null),"fallback"),"fallback");
        text(tests,context,"nullif",new NullIf("same","same"),null);
        text(tests,context,"ifnull",new IfNull(null,"fallback"),"fallback");
        text(tests,context,"if",new If(new Comparison(new Value(3),Comparison.Operator.GT,2),"yes","no"),"yes");
        text(tests,context,"case.simple",new CaseWhen(new Column(Account::getStatus))
                .when("ACTIVE","enabled").otherwise("disabled"),"enabled");
        text(tests,context,"case.searched",new CaseWhen()
                .when(new Comparison(new Column(Account::getBalance),Comparison.Operator.GT,100),"rich")
                .otherwise("normal"),"rich");
        text(tests,context,"case.noElse",new CaseWhen(new Value(1)).when(2,"missing"),null);
        text(tests,context,"case.hostile",new CaseWhen(new Value(1)).when(1,"O'Reilly ? : 中文"),"O'Reilly ? : 中文");
        number(tests,context,"cast.decimal",new Cast("12.345",CastTarget.decimal(8,2)),12.35);
        number(tests,context,"convert.signed",new Convert("42",CastTarget.SIGNED),42);
        text(tests,context,"cast.date",new Cast("2024-02-29",CastTarget.DATE),"2024-02-29");
        text(tests,context,"convert.charset",new Convert("中文",Convert.Charset.UTF8MB4),"中文");
        text(tests,context,"cast.null",new Cast(null,CastTarget.CHAR),null);
        text(tests,context,"json.string",new JsonUnquote(new JsonExtract(
                new Value("{\"name\":\"中文\"}"),"$.name")),"中文");
        text(tests,context,"json.missing",new JsonExtract(new Value("{}"),"$.missing"),null);
        text(tests,context,"json.null",new JsonExtract(new Value("{\"n\":null}"),"$.n"),"null");
        text(tests,context,"json.sqlNull",new JsonExtract(new Value(null),"$.n"),null);
        text(tests,context,"json.multi",new JsonExtract(new Value("{\"a\":1,\"b\":2}"),"$.a","$.b"),"[1, 2]");
        text(tests,context,"json.field",new JsonUnquote(new JsonExtract(new Column(MysqlTypeSample::getJsonValue),"$.name")),"中文",
                MysqlTypeSample.class,3L);
        text(tests,context,"divide.zero",new Value(1).divide(0),null);
        text(tests,context,"substring.hostile",new SubString("O'Reilly",1,3),"O'R");
        text(tests,context,"substring.null",new SubString((String)null,1,3),null);
        text(tests,context,"dateformat",new DateFormat(new Column(Account::getCreatedAt),"%Y-%m-%d"),"2024-02-29");
        text(tests,context,"json.object",new JsonExtract(new Value("{\"a\":{\"x\":1}}"),"$.a"),"{\"x\": 1}");
        tests.add(DynamicTest.dynamicTest("p0.where",()->assertEquals(Arrays.asList(100L),
                context.select().column(Account::getId).from(Account.class)
                        .where(w->w.andGreaterThan(new Cast(new Column(Account::getBalance),CastTarget.decimal(12,4)),0))
                        .fetch(Long.class).toList())));
        tests.add(DynamicTest.dynamicTest("p0.orderBy",()->assertEquals(Arrays.asList(100L,101L,103L,102L),
                context.select().column(Account::getId).from(Account.class)
                        .orderBy(new CaseWhen(new Column(Account::getStatus)).when("ACTIVE",0).otherwise(1))
                        .thenOrderBy(Account::getId).fetch(Long.class).toList())));
        tests.add(DynamicTest.dynamicTest("p0.having",()->assertEquals(Arrays.asList(20L),
                context.select().column(Account::getDepartmentId).from(Account.class)
                        .groupBy(Account::getDepartmentId)
                        .having(h->h.andGreaterThan(new Sum(new Cast(new Column(Account::getBalance),CastTarget.decimal(12,4))),0))
                        .fetch(Long.class).toList())));
        tests.add(DynamicTest.dynamicTest("p0.json.implicitEntityAlias",()->{
            MysqlTypeSample row=context.select().column(new JsonUnquote(new JsonExtract(MysqlTypeSample::getJsonValue,"$.name")))
                    .from(MysqlTypeSample.class).where(w->w.andEqualTo(MysqlTypeSample::getId,3L))
                    .fetch(MysqlTypeSample.class).toOne();
            assertEquals("中文",row.getJsonValue());
        }));
        text(tests,context,"arithmetic.null",new Value(null).add(1),null);
        text(tests,context,"case.multiMixed",new CaseWhen(new Value(2)).when(1,"one")
                .when(2,new Value(2)).otherwise("other"),"2");
        tests.add(DynamicTest.dynamicTest("p0.cast.binary",()->assertArrayEquals(
                "abc".getBytes(StandardCharsets.UTF_8),context.select().column(new Cast("abc",CastTarget.BINARY))
                        .from(Account.class).where(w->w.andEqualTo(Account::getId,100L)).fetch(byte[].class).toOne())));
        return tests.stream();
    }

    /** 数值用容差比较；同一表达式重复执行，确认渲染与参数容器稳定。 */
    private static void number(List<DynamicTest> tests,SqlContext context,String name,AbstractColumFunction expression,double expected) {
        tests.add(DynamicTest.dynamicTest("p0."+name,()->{
            for(int i=0;i<2;i++) assertEquals(expected,context.select().column(expression).from(Account.class)
                    .where(w->w.andEqualTo(Account::getId,100L)).fetch(Double.class).toOne(),1e-9);
        }));
    }
    private static void text(List<DynamicTest> tests,SqlContext context,String name,AbstractColumFunction expression,String expected) {
        text(tests,context,name,expression,expected,Account.class,100L);
    }
    /** 字符、日期和 SQL NULL 均通过指定标量类型映射。 */
    private static void text(List<DynamicTest> tests,SqlContext context,String name,AbstractColumFunction expression,
            String expected,Class<?> table,long id) {
        tests.add(DynamicTest.dynamicTest("p0."+name,()->{
            for(int i=0;i<2;i++) assertEquals(expected,context.select().column(expression).from(table)
                    .where(w->w.andEqualTo(new Column("id"),id)).fetch(String.class).toOne());
        }));
    }
}
