package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("class")
public class TClass {
    public int classId;
    public String className;
    public int gradeLevel;
    public int classTeacherId;
}