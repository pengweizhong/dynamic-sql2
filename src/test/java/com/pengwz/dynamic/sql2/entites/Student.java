package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.Table;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;
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

    public enum GenderEnum implements AttributeConverter<GenderEnum, String> {
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
        public String convertToDatabaseColumn(GenderEnum attribute) {
            return attribute.getGender();
        }

        @Override
        public GenderEnum convertToEntityAttribute(String dbData) {
            GenderEnum[] values = GenderEnum.values();
            for (GenderEnum value : values) {
                if (value.getGender().equals(dbData)) {
                    return value;
                }
            }
            return null;
        }
    }

    public static class GenderEnumConverter extends DefaultAttributeConverter<GenderEnum, String> {

        @Override
        protected String doConvertToDatabaseColumn(GenderEnum attribute) {
            return attribute.getGender();
        }

        @Override
        protected GenderEnum doConvertToEntityAttribute(String dbData) {
            GenderEnum[] values = GenderEnum.values();
            for (GenderEnum value : values) {
                if (value.getGender().equals(dbData)) {
                    return value;
                }
            }
            return null;
        }
    }
}
