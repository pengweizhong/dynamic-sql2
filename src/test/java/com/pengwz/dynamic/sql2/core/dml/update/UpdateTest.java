package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.SqlContext;
import com.pengwz.dynamic.sql2.entites.Student;
import org.junit.jupiter.api.Test;

class UpdateTest extends InitializingContext {
    SqlContext sqlContext = SqlContext.createSqlContext();

    @Test
    void test1() {
        sqlContext.update().update(new Student(), condition->condition.andEqualTo(Student::getStudentId,1));
    }
    @Test
    void test2() {
        sqlContext.update().update(new Student(), null);
    }
}