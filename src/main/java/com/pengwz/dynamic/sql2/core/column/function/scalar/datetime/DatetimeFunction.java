package com.pengwz.dynamic.sql2.core.column.function.scalar.datetime;

import com.pengwz.dynamic.sql2.core.column.function.scalar.ScalarFunction;

/**
 * •	CURRENT_DATE 或 CURDATE(): 返回当前日期。
 * •	CURRENT_TIME 或 CURTIME(): 返回当前时间。
 * •	DATEADD(date, interval, unit): 向日期加上指定的时间间隔。
 * •	DATEDIFF(date1, date2): 返回两个日期之间的天数。
 * •	YEAR(date): 返回日期的年份。
 * •	MONTH(date): 返回日期的月份。
 * •	DAY(date): 返回日期的天数。
 * •	DATE_FORMAT(date, format): 格式化日期为指定的格式。
 */
public interface DatetimeFunction extends ScalarFunction {


}
