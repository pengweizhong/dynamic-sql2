package com.pengwz.dynamic.sql2.entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subject {
    public int subjectId;
    public String subjectName;
    public String description;
    public int credits;
}