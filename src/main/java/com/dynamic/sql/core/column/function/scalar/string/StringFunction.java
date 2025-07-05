/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.string;


import com.dynamic.sql.core.column.function.scalar.ScalarFunction;

/**
 * •	CONCAT(str1, str2, ...): 连接两个或多个字符串。
 * •	SUBSTRING(str, start, length): 从字符串 str 中提取子串。
 * •	LENGTH(str) 或 LEN(str): 返回字符串的长度。
 * •	TRIM(str): 删除字符串前后的空格。
 * •	REPLACE(str, search, replace): 替换字符串中的子串。
 */
public interface StringFunction extends ScalarFunction {


}
