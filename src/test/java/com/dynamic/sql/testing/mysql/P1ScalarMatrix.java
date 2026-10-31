package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.Value;
import com.dynamic.sql.core.column.function.scalar.string.*;
import com.dynamic.sql.core.column.function.scalar.datetime.*;
import com.dynamic.sql.core.column.function.scalar.number.*;
import com.dynamic.sql.core.column.function.scalar.conversion.Cast;
import com.dynamic.sql.core.column.function.scalar.conversion.CastTarget;
import com.dynamic.sql.core.column.function.logical.*;
import com.dynamic.sql.core.column.function.json.*;
import com.dynamic.sql.testing.model.Account;
import org.junit.jupiter.api.DynamicTest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/** P1 标量逐函数以独立 JDBC 查询核对，所有框架值均走绑定及结果转换。 */
final class P1ScalarMatrix {
    private P1ScalarMatrix() { }
    static Stream<DynamicTest> cases(SqlContext context,Connection connection) {
        List<DynamicTest> tests=new ArrayList<>();
        add(tests,context,connection,"Lower",new Lower("ABC中文"),"LOWER('ABC中文')");
        add(tests,context,connection,"Concat",new Concat("a","中文"),"CONCAT('a','中文')");
        add(tests,context,connection,"Concat.null",new Concat("a",new Value(null)),"CONCAT('a',NULL)");
        add(tests,context,connection,"ConcatWs",new ConcatWs(",",new Value(null),"a","b"),"CONCAT_WS(',',NULL,'a','b')");
        add(tests,context,connection,"CharLength",new CharLength("中😀"),"CHAR_LENGTH('中😀')");
        add(tests,context,connection,"OctetLength",new OctetLength("中😀"),"OCTET_LENGTH('中😀')");
        add(tests,context,connection,"Trim",new Trim("  a  "),"TRIM('  a  ')");
        add(tests,context,connection,"LTrim",new LTrim("  a  "),"LTRIM('  a  ')");
        add(tests,context,connection,"RTrim",new RTrim("  a  "),"RTRIM('  a  ')");
        add(tests,context,connection,"Replace",new Replace("abcabc","a","中"),"REPLACE('abcabc','a','中')");
        add(tests,context,connection,"Left",new Left("中文abc",2),"LEFT('中文abc',2)");
        add(tests,context,connection,"Right",new Right("中文abc",2),"RIGHT('中文abc',2)");
        add(tests,context,connection,"LPad",new LPad("a",4,"中"),"LPAD('a',4,'中')");
        add(tests,context,connection,"RPad",new RPad("a",4,"中"),"RPAD('a',4,'中')");
        add(tests,context,connection,"LPad.truncate",new LPad("abcdef",3,"x"),"LPAD('abcdef',3,'x')");
        add(tests,context,connection,"Locate",new Locate("a","banana",3),"LOCATE('a','banana',3)");
        add(tests,context,connection,"Instr",new Instr("banana","na"),"INSTR('banana','na')");
        add(tests,context,connection,"Instr.missing",new Instr("abc","z"),"INSTR('abc','z')");
        add(tests,context,connection,"SubstringIndex",new SubstringIndex("a.b.c",".",-1),"SUBSTRING_INDEX('a.b.c','.',-1)");
        add(tests,context,connection,"Reverse",new Reverse("中文abc"),"REVERSE('中文abc')");
        add(tests,context,connection,"Repeat",new Repeat("a",-1),"REPEAT('a',-1)");
        add(tests,context,connection,"Date",new Date("2024-02-29 12:34:56"),"DATE('2024-02-29 12:34:56')");
        add(tests,context,connection,"Time",new Time("2024-02-29 12:34:56.123456"),"TIME('2024-02-29 12:34:56.123456')");
        add(tests,context,connection,"Year",new Year("2024-02-29"),"YEAR('2024-02-29')");
        add(tests,context,connection,"Month",new Month("2024-02-29"),"MONTH('2024-02-29')");
        add(tests,context,connection,"Day",new Day("2024-02-29"),"DAY('2024-02-29')");
        add(tests,context,connection,"Extract",new Extract(DateTimeUnit.MONTH,"2024-02-29"),"EXTRACT(MONTH FROM '2024-02-29')");
        add(tests,context,connection,"DateAdd",new DateAdd("2024-01-31",1,DateTimeUnit.MONTH),"DATE_ADD('2024-01-31',INTERVAL 1 MONTH)");
        add(tests,context,connection,"DateSub",new DateSub("2024-03-01",1,DateTimeUnit.DAY),"DATE_SUB('2024-03-01',INTERVAL 1 DAY)");
        add(tests,context,connection,"DateAdd.negative",new DateAdd("2024-03-01",-1,DateTimeUnit.DAY),"DATE_ADD('2024-03-01',INTERVAL -1 DAY)");
        add(tests,context,connection,"DateDiff",new DateDiff("2024-03-01","2024-02-28"),"DATEDIFF('2024-03-01','2024-02-28')");
        add(tests,context,connection,"TimestampDiff",new TimestampDiff(DateTimeUnit.HOUR,"2024-02-29 00:00:00","2024-03-01 00:00:00"),"TIMESTAMPDIFF(HOUR,'2024-02-29 00:00:00','2024-03-01 00:00:00')");
        add(tests,context,connection,"LastDay",new LastDay("2024-02-01"),"LAST_DAY('2024-02-01')");
        add(tests,context,connection,"StrToDate",new Cast(new StrToDate("29/02/2024","%d/%m/%Y"),CastTarget.DATE),"CAST(STR_TO_DATE('29/02/2024','%d/%m/%Y') AS DATE)");
        add(tests,context,connection,"UnixTimestamp",new Cast(new UnixTimestamp("2024-02-29 00:00:00"),CastTarget.decimal(20,6)),"CAST(UNIX_TIMESTAMP('2024-02-29 00:00:00') AS DECIMAL(20,6))");
        add(tests,context,connection,"FromUnixTime",new FromUnixTime(1709164800,"%Y-%m-%d"),"FROM_UNIXTIME(1709164800,'%Y-%m-%d')");
        add(tests,context,connection,"ConvertTz",new Cast(new ConvertTz("2024-02-29 00:00:00","+00:00","+08:00"),CastTarget.DATETIME),"CAST(CONVERT_TZ('2024-02-29 00:00:00','+00:00','+08:00') AS DATETIME)");
        add(tests,context,connection,"ConvertTz.invalid",new ConvertTz("2024-02-29","missing-zone","+00:00"),"CONVERT_TZ('2024-02-29','missing-zone','+00:00')");
        add(tests,context,connection,"Sign",new Sign(-123),"SIGN(-123)");
        add(tests,context,connection,"Power",new Power(2,10),"POWER(2,10)");
        add(tests,context,connection,"Sqrt",new Sqrt(4),"SQRT(4)");
        add(tests,context,connection,"Sqrt.invalid",new Sqrt(-1),"SQRT(-1)");
        add(tests,context,connection,"Exp",new Exp(0),"EXP(0)");
        add(tests,context,connection,"Ln",new Ln(1),"LN(1)");
        add(tests,context,connection,"Log",new Log(2,8),"LOG(2,8)");
        add(tests,context,connection,"Greatest",new Greatest(1,3,2),"GREATEST(1,3,2)");
        add(tests,context,connection,"Least",new Least(1,3,2),"LEAST(1,3,2)");
        add(tests,context,connection,"Greatest.null",new Greatest(1,new Value(null)),"GREATEST(1,NULL)");
        add(tests,context,connection,"IsNull",new IsNull(new Value(null)),"ISNULL(NULL)");
        add(tests,context,connection,"JsonObject",new JsonObject("name","中文","n",new Value(null)),"JSON_OBJECT('name','中文','n',NULL)");
        add(tests,context,connection,"JsonObject.empty",new JsonObject(),"JSON_OBJECT()");
        add(tests,context,connection,"JsonArray",new JsonArray(1,"a",new Value(null)),"JSON_ARRAY(1,'a',NULL)");
        add(tests,context,connection,"JsonType",new JsonType("[1,2]"),"JSON_TYPE('[1,2]')");
        add(tests,context,connection,"JsonLength",new JsonLength("{\"a\":[1,2]}","$.a"),"JSON_LENGTH('{\"a\":[1,2]}','$.a')");
        add(tests,context,connection,"JsonValid",new JsonValid("{bad"),"JSON_VALID('{bad')");
        add(tests,context,connection,"JsonKeys",new JsonKeys("{\"a\":1}"),"JSON_KEYS('{\"a\":1}')");
        add(tests,context,connection,"JsonSet",new JsonSet("{}","$.a",1,"$.b",2),"JSON_SET('{}','$.a',1,'$.b',2)");
        add(tests,context,connection,"JsonInsert",new JsonInsert("{\"a\":1}","$.a",2,"$.b",3),"JSON_INSERT('{\"a\":1}','$.a',2,'$.b',3)");
        add(tests,context,connection,"JsonReplace",new JsonReplace("{\"a\":1}","$.a",2),"JSON_REPLACE('{\"a\":1}','$.a',2)");
        add(tests,context,connection,"JsonRemove",new JsonRemove("{\"a\":1,\"b\":2}","$.a"),"JSON_REMOVE('{\"a\":1,\"b\":2}','$.a')");
        add(tests,context,connection,"JsonContains",new JsonContains("[1,2]","1"),"JSON_CONTAINS('[1,2]','1')");
        add(tests,context,connection,"JsonContainsPath",new JsonContainsPath("{\"a\":1}","one","$.a"),"JSON_CONTAINS_PATH('{\"a\":1}','one','$.a')");
        add(tests,context,connection,"JsonSearch",new JsonSearch("[\"a\",\"b\"]","one","b"),"JSON_SEARCH('[\"a\",\"b\"]','one','b')");
        add(tests,context,connection,"JsonMergePatch",new JsonMergePatch("{\"a\":1}","{\"a\":null,\"b\":2}"),"JSON_MERGE_PATCH('{\"a\":1}','{\"a\":null,\"b\":2}')");
        add(tests,context,connection,"Round",new Round(new Value(-1.235),2),"ROUND(-1.235,2)");
        add(tests,context,connection,"Truncate",new Truncate(new Value(-1.239),2),"TRUNCATE(-1.239,2)");
        add(tests,context,connection,"CurrentDate",new CurrentDate(),"CURRENT_DATE()");
        add(tests,context,connection,"CurrentTime.statementStable",new Comparison(new CurrentTime(),Comparison.Operator.EQ,
                new Time(new Now())),"CURRENT_TIME()=TIME(NOW())");
        add(tests,context,connection,"UtcTimestamp.statementStable",new Comparison(new UtcTimestamp(),Comparison.Operator.EQ,
                new ConvertTz(new Now(),"+00:00","+00:00")),"UTC_TIMESTAMP()=CONVERT_TZ(NOW(),'+00:00','+00:00')");
        add(tests,context,connection,"CurrentTime.precision",new Comparison(new CurrentTime(6),Comparison.Operator.EQ,
                new CurrentTime(6)),"CURRENT_TIME(6)=CURRENT_TIME(6)");
        add(tests,context,connection,"UtcTimestamp.precision",new Comparison(new UtcTimestamp(6),Comparison.Operator.EQ,
                new UtcTimestamp(6)),"UTC_TIMESTAMP(6)=UTC_TIMESTAMP(6)");
        add(tests,context,connection,"ConvertTz.namedDst",new Cast(new ConvertTz("2024-03-10 07:30:00","+00:00","America/New_York"),CastTarget.DATETIME),
                "CAST(CONVERT_TZ('2024-03-10 07:30:00','+00:00','America/New_York') AS DATETIME)");
        return tests.stream();
    }

    /** 转成文本后与同连接 JDBC 比较，避开驱动返回类差异；重复执行检验参数稳定。 */
    private static void add(List<DynamicTest> tests,SqlContext context,Connection connection,
            String name,AbstractColumFunction function,String reference) {
        tests.add(DynamicTest.dynamicTest("p1.scalar."+name,()->{
            String expected;
            try(Statement statement=connection.createStatement();ResultSet result=statement.executeQuery("SELECT CAST("+reference+" AS CHAR)")) {
                assertTrue(result.next());expected=result.getString(1);
            }
            for(int i=0;i<2;i++) assertEquals(expected,context.select().column(new Cast(function,CastTarget.CHAR))
                    .from(Account.class).where(w->w.andEqualTo(Account::getId,100L)).fetch(String.class).toOne());
        }));
    }
}
