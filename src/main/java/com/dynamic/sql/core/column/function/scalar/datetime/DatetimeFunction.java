/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.datetime;


import com.dynamic.sql.core.column.function.scalar.ScalarFunction;

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
