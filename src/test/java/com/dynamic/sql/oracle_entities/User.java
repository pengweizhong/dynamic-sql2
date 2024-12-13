package com.dynamic.sql.oracle_entities;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import com.dynamic.sql.entites.enums.Gender;
import com.dynamic.sql.entites.enums.UserStatus;
import com.dynamic.sql.plugins.conversion.AttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("USERS")
public class User {
    @Id
    @GeneratedValue
    private Long userId;  // 用户 ID
    private String name;  // 用户姓名
    private Gender gender;  // 性别
    @Column(value = "REGISTRATION_DATE", format = "yyyy-MM-dd")
    private Date registrationDate;  // 注册日期
    private String email;  // 邮箱
    private String phoneNumber;  // 电话号码
    private double accountBalance;  // 账户余额
    //    @Column(converter = ClobAttributeConverter.class)
    private String details;  // JSON 格式的额外信息
    private UserStatus status;  // 用户状态

    public static class ClobAttributeConverter implements AttributeConverter {

        @Override
        public Object convertToDatabaseColumn(Object attribute) {
            return attribute;
        }

        @Override
        public Object convertToEntityAttribute(Object dbData) {
//            StringBuilder sb = new StringBuilder();
//            Clob clob = (Clob) dbData;
//            try {
//                try (Reader reader = clob.getCharacterStream();
//                     BufferedReader br = new BufferedReader(reader)) {
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line);
//                    }
//                }
//                return sb.toString();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            return dbData;
        }
    }
}