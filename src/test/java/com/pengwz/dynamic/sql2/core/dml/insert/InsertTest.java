package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.SqlContext;
import com.pengwz.dynamic.sql2.entites.Student;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class InsertTest extends InitializingContext {

    SqlContext sqlContext = SqlContext.createSqlContext();

    @Test
    void insert1() {
        sqlContext.insert().insert(new Student());
    }

    @Test
    void insert2() {
        sqlContext.insert().insertSelective(new Student());
    }

    @Test
    void insert3() {
        sqlContext.insert().insertSelective(new Student(), Arrays.asList(Student::getStudentId, Student::getClassId));
    }
}