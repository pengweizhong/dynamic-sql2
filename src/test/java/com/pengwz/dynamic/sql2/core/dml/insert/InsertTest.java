//package com.pengwz.dynamic.sql2.core.dml.insert;
//
//import com.pengwz.dynamic.sql2.InitializingContext;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//
//class InsertTest extends InitializingContext {
//
//    @Test
//    void insert1() {
//        sqlContext.insert(new Student());
//    }
//
//    @Test
//    void insert2() {
//        sqlContext.insertSelective(new Student());
//    }
//
//    @Test
//    void insert3() {
//        sqlContext.insertSelective(new Student(), Arrays.asList(Student::getStudentId, Student::getClassId));
//    }
//}