package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.Table;
import com.dynamic.sql.entites.enums.Gender;
import com.dynamic.sql.entites.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("${com.db.user:users}")
public class User {
    private int userId;  // 用户 ID
    private String name;  // 用户姓名
    private Gender gender;  // 性别
    private Integer sex;  // 性别
    @Column()
    private Date registrationDate;  // 注册日期
    private String email;  // 邮箱
    private String phoneNumber;  // 电话号码
    private Double accountBalance;  // 账户余额
    private String details;  // JSON 格式的额外信息
    private UserStatus status;  // 用户状态
}