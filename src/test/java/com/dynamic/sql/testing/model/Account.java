package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import lombok.Data;

@Data
@Table("ds_account")
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private Long departmentId;
    private String loginName;
    private String displayName;
    private String status;
    private java.math.BigDecimal balance;
    private Integer versionNo;
    private Short deleted;
    private java.time.LocalDateTime createdAt;
}
