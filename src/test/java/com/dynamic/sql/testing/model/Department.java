package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import lombok.Data;

/**
 * 部门测试实体，覆盖自关联及通用 CRUD 行为。
 */
@Data
@Table("ds_department")
public class Department {
    @Id
    //固定主键，便于跨数据库复用相同样本。
    private Long id;
    //自关联父部门主键，根部门为 NULL。
    private Long parentId;
    //部门名称，写入测试包含中文和特殊字符。
    private String name;
}
