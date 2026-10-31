package com.dynamic.sql.testing.unit;

import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.utils.SqlUtils;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class PreparedSqlTest {
    @Test void parametersFollowSqlOrderIncludingRepeatedValues() {
        ParameterBinder binder = new ParameterBinder();
        String first = SqlUtils.registerValueWithKey(binder, 10);
        String second = SqlUtils.registerValueWithKey(binder, 20);
        PreparedSql sql = SqlUtils.parsePreparedObject(new StringBuilder(
                "SELECT * FROM sample WHERE a=" + second + " AND b=" + first + " OR c=" + second), binder);
        assertEquals("SELECT * FROM sample WHERE a=? AND b=? OR c=?", sql.getSql());
        assertEquals(Arrays.asList(20, 10, 20), sql.getParams());
    }

    @Test void hostileTextRemainsAParameter() {
        String value = "中文😀 O'Reilly'; DROP TABLE sample; -- % _ ?";
        ParameterBinder binder = new ParameterBinder();
        String key = SqlUtils.registerValueWithKey(binder, value);
        PreparedSql sql = SqlUtils.parsePreparedObject(new StringBuilder("SELECT * FROM sample WHERE name=" + key), binder);
        assertEquals("SELECT * FROM sample WHERE name=?", sql.getSql());
        assertEquals(Arrays.asList(value), sql.getParams());
    }

    @Test void nullIsBoundWithoutStringConversion() {
        ParameterBinder binder = new ParameterBinder();
        String key = SqlUtils.registerValueWithKey(binder, null);
        PreparedSql sql = SqlUtils.parsePreparedObject(new StringBuilder("INSERT INTO sample(value) VALUES (" + key + ")"), binder);
        assertEquals("INSERT INTO sample(value) VALUES (?)", sql.getSql());
        assertEquals(1, sql.getParams().size());
        assertNull(sql.getParams().get(0));
    }
}
