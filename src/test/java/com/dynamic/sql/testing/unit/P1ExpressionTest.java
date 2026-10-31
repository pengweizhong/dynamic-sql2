package com.dynamic.sql.testing.unit;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.json.JsonObject;
import com.dynamic.sql.core.column.function.json.JsonSet;
import com.dynamic.sql.core.column.function.scalar.datetime.*;
import com.dynamic.sql.core.column.function.string.GroupConcat;
import com.dynamic.sql.core.column.function.table.JsonTable;
import com.dynamic.sql.core.column.function.windows.*;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/** P1 不依赖数据库的语法白名单、参数绑定和非法输入契约。 */
class P1ExpressionTest {
    private RenderContext context() {
        RenderContext context=new RenderContext();context.setSqlDialect(SqlDialect.MYSQL);
        context.setVersion(new Version(8,4,7));return context;
    }
    /** 键值配对和必需参数在构造阶段校验。 */
    @Test void rejectsWrongArity() {
        assertThrows(IllegalArgumentException.class,()->new JsonObject("key"));
        assertThrows(IllegalArgumentException.class,()->new JsonSet("{}","$.a"));
        assertThrows(IllegalArgumentException.class,()->new Lag());
        assertThrows(IllegalArgumentException.class,()->new CumeDist(1));
    }
    /** 日期单位来自枚举，业务间隔不拼接到 SQL。 */
    @Test void dateUnitAndParameterOrder() {
        DateAdd expression=new DateAdd("2024-01-31",-1,DateTimeUnit.MONTH);
        String sql=expression.render(context());
        assertEquals(Arrays.asList("2024-01-31",-1),new ArrayList<>(expression.parameterBinder().getValues()));
        assertTrue(sql.contains("INTERVAL"));assertTrue(sql.contains("MONTH"));assertFalse(sql.contains("2024"));
        assertThrows(NullPointerException.class,()->new DateAdd("2024-01-01",1,null));
        assertThrows(IllegalArgumentException.class,()->new CurrentTime(7));
    }
    /** 窗口起点不能位于终点之后，偏移不可为负。 */
    @Test void rejectsInvalidWindowFrames() {
        assertThrows(IllegalArgumentException.class,()->Over.Bound.preceding(-1));
        assertThrows(IllegalArgumentException.class,()->new Over().rowsBetween(Over.Bound.following(2),Over.Bound.currentRow()));
        assertThrows(IllegalArgumentException.class,()->new Over().rangeBetween(Over.Bound.unboundedFollowing(),Over.Bound.unboundedFollowing()));
    }
    /** 排名扩展不能发送到不支持窗口函数的 MySQL 旧版本。 */
    @Test void rejectsOldWindowVersion() {
        RenderContext old=context();old.setVersion(new Version(5,7,44));
        assertThrows(UnsupportedOperationException.class,()->new Lag(new Value(1)).render(old));
    }
    /** 分隔符作为十六进制字面量处理，在不同转义模式下不改变结构。 */
    @Test void groupConcatSeparatorsAreEncoded() {
        String separator="'"+(char)92+"中文";
        GroupConcat expression=new GroupConcat(new Value("x"),separator);
        String sql=expression.render(context());
        assertFalse(sql.contains(separator));assertTrue(sql.contains("SEPARATOR X'"));
        assertEquals(sql,expression.render(context()));
    }
    /** JSON_TABLE 的标识符、类型和路径分别限制，默认值使用 JSON 标量编码。 */
    @Test void jsonTableRejectsSyntaxInjection() {
        JsonTable table=new JsonTable(new Value("[1]"),"$[*]",JsonTable.JsonColumn.builder()
                .column("n").dataType("INT); DROP TABLE x; --").jsonPath("$").build());
        assertThrows(IllegalArgumentException.class,()->table.render(context()));
        assertThrows(IllegalArgumentException.class,()->MysqlLiteral.identifier("x;--"));
        assertThrows(IllegalArgumentException.class,()->MysqlLiteral.text("$"+(char)92+"x"));
        assertEquals("'a''b'",MysqlLiteral.text("a'b"));
        assertEquals("\"a'b\"",MysqlLiteral.jsonScalar("a'b"));
    }
}
