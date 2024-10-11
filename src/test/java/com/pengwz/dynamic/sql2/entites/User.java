package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Table;
import com.pengwz.dynamic.sql2.entites.enums.Gender;
import com.pengwz.dynamic.sql2.entites.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class User {
    private int userId;  // 用户 ID
    private String name;  // 用户姓名
    private Gender gender;  // 性别
    private Date registrationDate;  // 注册日期
    private String email;  // 邮箱
    private String phoneNumber;  // 电话号码
    private double accountBalance;  // 账户余额
    private String details;  // JSON 格式的额外信息
    private UserStatus status;  // 用户状态
}