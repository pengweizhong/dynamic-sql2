/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import com.dynamic.sql.enums.GenerationType;
import lombok.Data;

/** 应用侧主键测试实体，复用部门物理表，不依赖数据库自增。 */
@Data
@Table("ds_department")
public class SnowflakeDepartment {
    @Id
    @GeneratedValue(strategy=GenerationType.SNOWFLAKE,generator="mysql-snowflake")
    private Long id;
    private String name;
}
