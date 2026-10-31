package com.dynamic.sql.core.database;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JDBC 查询结果行，同时保存原始列值与时间类型的补充信息。
 * <p>
 * Map 中仍存放 JDBC 返回值，避免改变原始 Map 查询的取值方式。
 * 时间文本单独保存，在映射 LocalTime、LocalDateTime 时使用，防止微秒丢失或 JVM 时区引起偏移。
 */
public final class JdbcResultRow extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    //以结果集列标签为键，包含 SQL 别名；存在键且值为 null 表示该时间列为 SQL NULL。
    private final Map<String, String> temporalValues = new HashMap<>();

    /**
     * 清除同名列之前保存的时间信息，保证重复列标签被覆盖时，补充信息与最终列值一致。
     */
    void clearTemporal(String column) {
        temporalValues.remove(column);
    }

    /**
     * 保存数据库会话中读取到的时间文本，不替换 Map 内的原始 JDBC 值。
     */
    void putTemporal(String column, String value) {
        temporalValues.put(column, value);
    }

    /**
     * 根据目标 Java 类型，选择用于映射的列值。
     * <p>
     * 只有 Java 时间类型使用补充信息，普通 Map 或其他目标类型仍使用原始值。
     * LocalDateTime 表示数据库会话中的本地时间，不在此处转换为 UTC 时间点。
     *
     * @param row    查询结果行，也允许传入普通 Map
     * @param column 结果集列标签，可能为 SQL 别名
     * @param target 目标字段类型或标量返回类型
     * @return 适合目标类型的时间值，或原始 JDBC 值；SQL NULL 返回 null
     */
    public static Object valueFor(Map<String, Object> row, String column, Class<?> target) {
        if (row instanceof JdbcResultRow && (target == LocalTime.class || target == LocalDateTime.class)) {
            JdbcResultRow jdbcRow = (JdbcResultRow) row;
            if (jdbcRow.temporalValues.containsKey(column)) {
                String text = jdbcRow.temporalValues.get(column);
                if (text == null) return null;
                //MySQL TIME 可以表示时长；负数或超过一天的值不能转为 LocalTime，由解析器明确报错。
                //这里不做取模或截断，避免把时长误当成一天内的时间。
                return target == LocalTime.class ? LocalTime.parse(text)
                        : LocalDateTime.parse(text.replace(' ', 'T'));
            }
        }
        return row.get(column);
    }
}
