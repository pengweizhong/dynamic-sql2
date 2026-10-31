package com.dynamic.sql.testing.unit;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.logical.*;
import com.dynamic.sql.core.column.function.scalar.conversion.*;
import com.dynamic.sql.core.column.function.json.JsonExtract;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** P0 无数据库契约：SQL 结构、绑定安全、版本边界与非法输入拒绝。 */
class P0ExpressionTest {
    private RenderContext mysql(int major,int minor,int patch) {
        RenderContext context=new RenderContext();context.setSqlDialect(SqlDialect.MYSQL);
        context.setVersion(new Version(major,minor,patch));return context;
    }
    private PreparedSql prepared(AbstractColumFunction expression) {
        return SqlUtils.parsePreparedObject(new StringBuilder(expression.render(mysql(8,4,7))),expression.parameterBinder());
    }

    /** 重复常量应保持独立占位位置，不因重复值去重而丢失参数。 */
    @Test void arithmeticBindsAndRendersRepeatedly() {
        AbstractColumFunction expression=new Value(2).add(new Value(2)).multiply(3);
        PreparedSql first=prepared(expression),second=prepared(expression);
        assertEquals("? + (?) * (?)",first.getSql());
        assertEquals(Arrays.asList(2,2,3),first.getParams());
        assertEquals(first.getSql(),second.getSql());assertEquals(first.getParams(),second.getParams());
    }
    /** 显式括号是可组合表达式，不通过业务 SQL 字符串插入。 */
    @Test void groupingPreservesPrecedence() {
        PreparedSql sql=prepared(new Group(new Value(2).add(3)).multiply(4));
        assertEquals("(? + (?)) * (?)",sql.getSql());
        assertEquals(Arrays.asList(2,3,4),sql.getParams());
    }
    /** CASE 条件及返回值中的特殊字符仅进入参数集合。 */
    @Test void caseKeepsHostileValuesOutOfSql() {
        String hostile="' OR 1=1 --";
        PreparedSql sql=prepared(new CaseWhen(new Value(1)).when(1,hostile).otherwise(null));
        assertFalse(sql.getSql().contains(hostile));
        assertEquals(Arrays.asList(1,1,hostile,null),sql.getParams());
    }
    @Test void rejectsInvalidCaseAndArithmetic() {
        assertThrows(IllegalStateException.class,()->prepared(new CaseWhen()));
        assertThrows(IllegalArgumentException.class,()->new CaseWhen().when(true,1));
        Value value=new Value(1);
        assertThrows(IllegalArgumentException.class,()->value.add(value));
        assertThrows(IllegalStateException.class,()->new CaseWhen(1).otherwise(2).otherwise(3));
    }
    /** 精度与字符长度属于受控 SQL 类型声明，非法范围在执行前拒绝。 */
    @Test void conversionValidatesTypeDeclaration() {
        assertThrows(IllegalArgumentException.class,()->CastTarget.decimal(66,2));
        assertThrows(IllegalArgumentException.class,()->CastTarget.decimal(10,11));
        assertThrows(IllegalArgumentException.class,()->CastTarget.character(0));
        assertThrows(NullPointerException.class,()->new Cast("1",null));
        assertEquals("CAST(? AS DECIMAL(8,2))",prepared(new Cast("1.23",CastTarget.decimal(8,2))).getSql());
    }
    /** JSON 路径和文档都绑定，拒绝把旧版 JSON_VALUE 当成提取对象的替代。 */
    @Test void jsonBindsPathAndRejectsUnsupportedVersions() {
        String path="$.\"O'Reilly\"";
        JsonExtract expression=new JsonExtract(new Value("{}"),path);
        assertEquals(Arrays.asList("{}",path),prepared(expression).getParams());
        assertThrows(RuntimeException.class,()->expression.render(mysql(5,6,99)));
        assertThrows(RuntimeException.class,()->expression.render(mysql(5,7,7)));
        assertDoesNotThrow(()->expression.render(mysql(5,7,8)));
        RenderContext oracle=new RenderContext();oracle.setSqlDialect(SqlDialect.ORACLE);
        assertThrows(RuntimeException.class,()->expression.render(oracle));
    }
}
