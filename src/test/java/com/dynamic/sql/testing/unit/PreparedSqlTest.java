package com.dynamic.sql.testing.unit;

import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.utils.SqlUtils;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 预编译 SQL 的参数绑定测试，无需数据库连接。
 */
class PreparedSqlTest {
    /**
     * 验证参数按 SQL 中的出现顺序绑定，同一占位符重复出现时也要重复提供参数。
     */
    @Test void parametersFollowSqlOrderIncludingRepeatedValues() {
        ParameterBinder binder = new ParameterBinder();
        String first = SqlUtils.registerValueWithKey(binder, 10);
        String second = SqlUtils.registerValueWithKey(binder, 20);
        PreparedSql sql = SqlUtils.parsePreparedObject(new StringBuilder(
                "SELECT * FROM sample WHERE a=" + second + " AND b=" + first + " OR c=" + second), binder);
        assertEquals("SELECT * FROM sample WHERE a=? AND b=? OR c=?", sql.getSql());
        assertEquals(Arrays.asList(20, 10, 20), sql.getParams());
    }

    /**
     * 包含引号、注释符号和中文的输入只能作为参数传递，不能拼接进 SQL。
     */
    @Test void hostileTextRemainsAParameter() {
        String value = "中文😀 O'Reilly'; DROP TABLE sample; -- % _ ?";
        ParameterBinder binder = new ParameterBinder();
        String key = SqlUtils.registerValueWithKey(binder, value);
        PreparedSql sql = SqlUtils.parsePreparedObject(new StringBuilder("SELECT * FROM sample WHERE name=" + key), binder);
        assertEquals("SELECT * FROM sample WHERE name=?", sql.getSql());
        assertEquals(Arrays.asList(value), sql.getParams());
    }

    /**
     * NULL 作为参数原样保留，不能转换为字符串或丢失占位符。
     */
    @Test void nullIsBoundWithoutStringConversion() {
        ParameterBinder binder = new ParameterBinder();
        String key = SqlUtils.registerValueWithKey(binder, null);
        PreparedSql sql = SqlUtils.parsePreparedObject(new StringBuilder("INSERT INTO sample(value) VALUES (" + key + ")"), binder);
        assertEquals("INSERT INTO sample(value) VALUES (?)", sql.getSql());
        assertEquals(1, sql.getParams().size());
        assertNull(sql.getParams().get(0));
    }
}
