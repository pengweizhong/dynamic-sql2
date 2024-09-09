package com.pengwz.dynamic.sql2.entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {
    public int studentId;
    public String firstName;
    public String lastName;
    public String gender;
    public Date birthDate;
    public Date enrollmentDate;
    public int classId;
}
