package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import com.dynamic.sql.enums.GenerationType;
import lombok.Data;

/** 应用侧主键测试实体，复用部门物理表，不依赖数据库自增。 */
@Data
@Table("ds_department")
public class StringSnowflakeDepartment {
    @Id
    @GeneratedValue(strategy=GenerationType.SNOWFLAKE,generator="mysql-snowflake")
    private String id;
    private String name;
}
