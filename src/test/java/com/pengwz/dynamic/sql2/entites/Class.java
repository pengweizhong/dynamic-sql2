package com.pengwz.dynamic.sql2.entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Class {
    public int classId;
    public String className;
    public int gradeLevel;
    public int classTeacherId;
}