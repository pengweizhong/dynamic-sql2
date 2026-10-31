package com.dynamic.sql.core.column.function.scalar.datetime;

/** 时间单位属于 SQL 语法，使用枚举禁止业务字符串注入。 */
public enum DateTimeUnit { MICROSECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, YEAR }
