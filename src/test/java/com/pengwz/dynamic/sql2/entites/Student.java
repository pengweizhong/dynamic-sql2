package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.Table;
import com.pengwz.dynamic.sql2.plugins.conversion.EnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("student")
public class Student {
    public int studentId;
    public String firstName;
    public String lastName;
    // 枚举转换有两种方案，第一种是枚举直接继承EnumConverter，
    // 第二种是自定义单独实现类 @Column(converter = GenderEnumConverter.class)
    // 同时存在时注解内生效
    @Column(converter = GenderEnumConverter.class)
    public GenderEnum gender;
    public Date birthDate;
    public Date enrollmentDate;
    public int classId;

    public enum GenderEnum implements EnumConverter<GenderEnum, String> {
        Female("女"), Male("男");
        private final String gender;

        GenderEnum(String gender) {
            this.gender = gender;
        }

        public static GenderEnum readGender(String gender) {
            return valueOf(gender);
        }

        public static String writeGender(GenderEnum genderEnum) {
            return genderEnum.name();
        }

        public String getGender() {
            return gender;
        }

        @Override
        public String toDatabaseValue(GenderEnum attribute) {
            return attribute.gender;
        }

        @Override
        public GenderEnum fromDatabaseValue(String dbData) {
            return GenderEnum.valueOf(gender);
        }
    }

    public static class GenderEnumConverter implements EnumConverter<GenderEnum, String> {

        @Override
        public String toDatabaseValue(GenderEnum attribute) {
            return attribute.gender;
        }

        @Override
        public GenderEnum fromDatabaseValue(String gender) {
            return GenderEnum.valueOf(gender);
        }
    }
}
