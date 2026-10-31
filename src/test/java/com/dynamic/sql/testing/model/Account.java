package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import lombok.Data;

/**
 * 账户测试实体，覆盖自增主键、默认值、数值转换和部门关联。
 */
@Data
@Table("ds_account")
public class Account {
    @Id
    @GeneratedValue
    //数据库生成主键，验证插入后主键回填。
    private Long id;
    //可空部门外键，用于验证未关联记录的 LEFT JOIN 行为。
    private Long departmentId;
    //唯一登录名，用于验证查询条件和唯一约束。
    private String loginName;
    //可空显示名称，用于区分 NULL 与普通文本。
    private String displayName;
    //账户状态，选择性插入时由数据库提供默认值。
    private String status;
    //精确金额，不使用浮点数接收。
    private java.math.BigDecimal balance;
    //版本字段，本实体用于验证默认值，不代表已启用乐观锁。
    private Integer versionNo;
    //SMALLINT 标志，覆盖 JDBC Integer 到 Short 的转换。
    private Short deleted;
    //微秒创建时间，同时作为稳定分页的第一排序字段。
    private java.time.LocalDateTime createdAt;
}
