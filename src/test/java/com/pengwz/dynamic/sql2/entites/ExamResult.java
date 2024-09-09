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
@Table("exam_result")
public class ExamResult {
    public int resultId;
    public int studentId;
    public int examId;
    public int subjectId;
    public double score;
    public String grade;
}