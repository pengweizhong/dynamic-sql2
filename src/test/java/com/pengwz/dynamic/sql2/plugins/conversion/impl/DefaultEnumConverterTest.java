//package com.pengwz.dynamic.sql2.plugins.conversion.impl;
//
//import com.pengwz.dynamic.sql2.entites.Student;
//import org.junit.jupiter.api.Test;
//
//class DefaultEnumConverterTest {
//
//    @Test
//    void convert() {
//        DefaultEnumConverter defaultEnumConverter = new DefaultEnumConverter();
//        Enum female = defaultEnumConverter.fromDatabaseValue(Student.GenderEnum.class, "Female");
//        System.out.println(female);
//    }
//
//    @Test
//    void convert2() {
//        DefaultEnumConverter defaultEnumConverter = new DefaultEnumConverter();
//        Enum female = defaultEnumConverter.fromDatabaseValue(Student.GenderEnum.class, "FEMALE");
//        System.out.println(female);
//    }
//
//    @Test
//    void convert3() {
//        DefaultEnumConverter defaultEnumConverter = new DefaultEnumConverter();
//        Object databaseValue = defaultEnumConverter.toDatabaseValue(Student.GenderEnum.Male);
//        System.out.println(databaseValue);
//    }
//}