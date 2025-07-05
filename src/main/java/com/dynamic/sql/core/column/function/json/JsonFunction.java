/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.json;


import com.dynamic.sql.core.column.function.ColumFunction;

/**
 * 处理 JSON 数据的函数接口。
 * -- 	•	查询数据: 从 JSON 列中查询数据
 * SELECT data->>"$.name" AS name FROM my_table;
 * -- 	•	JSON_EXTRACT(): 提取 JSON 数据中的值
 * SELECT JSON_EXTRACT(data, '$.name') AS name FROM my_table;
 * -- 	•	JSON_UNQUOTE(): 去掉 JSON 值的引号
 * SELECT JSON_UNQUOTE(JSON_EXTRACT(data, '$.name')) AS name FROM my_table;
 * -- 	•	JSON_SET(): 更新 JSON 数据中的值
 * UPDATE my_table SET data = JSON_SET(data, '$.age', 31) WHERE id = 1;
 * -- 	•	JSON_ARRAY(): 创建 JSON 数组
 * SELECT JSON_ARRAY(1, 2, 3) AS numbers;
 * -- 	•	JSON_OBJECT(): 创建 JSON 对象
 * SELECT JSON_OBJECT('name', 'John', 'age', 30) AS person;
 * -- 	•	JSON_CONTAINS(): 检查 JSON 数据是否包含某个值
 * SELECT JSON_CONTAINS(data, '"John"', '$.name') AS contains_name FROM my_table;
 * -- 	•	JSON_REMOVE(): 从 JSON 数据中移除指定路径的键
 * UPDATE my_table SET data = JSON_REMOVE(data, '$.age') WHERE id = 1;
 * -- 	•	JSON_MERGE(): 合并多个 JSON 文档
 * SELECT JSON_MERGE('{"name": "John"}', '{"age": 30}') AS merged_data;
 * <p>
 * <p>
 * -- 	•	虚拟列索引: 为 JSON 列中的特定字段创建索引，以提高查询性能
 * ALTER TABLE my_table ADD COLUMN name VARCHAR(100) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.name'))) STORED;
 * CREATE INDEX idx_name ON my_table(name);
 * <p>
 * <p>
 * -- 	•	查询 JSON 数据中的某个字段
 * SELECT * FROM my_table WHERE JSON_EXTRACT(data, '$.name') = 'John';
 * -- 	•	筛选 JSON 数组中的特定元素
 * SELECT * FROM my_table WHERE JSON_CONTAINS(data, '"John"', '$.names');
 */
public interface JsonFunction extends ColumFunction {
}
