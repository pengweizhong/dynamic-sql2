package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(value = "teacher",alias = "t1")
public class Teacher {
    public int teacherId;
    public String firstName;
    public String lastName;
    public String gender;
    public Date birthDate;
    public Date hireDate;
    public String department;
}
