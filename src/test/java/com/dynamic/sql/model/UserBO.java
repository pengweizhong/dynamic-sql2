package com.dynamic.sql.model;

import com.dynamic.sql.entites.UserExt;
import com.dynamic.sql.entites.enums.Gender;
import com.dynamic.sql.entites.enums.UserStatus;
import lombok.Data;

import java.util.Date;

@Data
public class UserBO {
    private int userId;  // 用户 ID
    private String name;  // 用户姓名
    private Gender gender;  // 性别
    private Integer sex;  // 性别
    private Date registrationDate;  // 注册日期
    private String email;  // 邮箱
    private String phoneNumber;  // 电话号码
    private Double accountBalance;  // 账户余额
    private String details;  // JSON 格式的额外信息
    private UserStatus status;  // 用户状态
    private UserExt userExt;
}
