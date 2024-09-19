package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.Table;
import com.pengwz.dynamic.sql2.conversion.EnumConverter;
import com.pengwz.dynamic.sql2.utils.MapUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.pengwz.dynamic.sql2.entites.Student.GenderEnum.Female;
import static com.pengwz.dynamic.sql2.entites.Student.GenderEnum.Male;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("student")
public class Student {
    public int studentId;
    public String firstName;
    public String lastName;
    @Column(converter = GenderEnumConverter.class)
    public GenderEnum gender;
    public Date birthDate;
    public Date enrollmentDate;
    public int classId;

    public enum GenderEnum {
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
    }

    public static class GenderEnumConverter extends EnumConverter<GenderEnum, String> {


        // 定义数据库值和枚举值之间的映射关系
        public GenderEnumConverter() {
            super(MapUtils.of("女", Female, "男", Male));
        }
    }
}
