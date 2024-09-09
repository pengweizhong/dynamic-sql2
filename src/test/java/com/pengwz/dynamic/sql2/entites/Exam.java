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
public class Exam {
    public int examId;
    public int subjectId;
    public Date examDate;
    public String examType;
}
