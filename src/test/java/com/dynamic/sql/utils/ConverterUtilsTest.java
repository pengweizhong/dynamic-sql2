package com.dynamic.sql.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

class ConverterUtilsTest {

    @Test
    void formatterDateValue() {
        // LocalDate类型，只有日期部分
        System.out.println(ConverterUtils.formatterDateValue(LocalDate.now(), "yyyy-MM-dd")); // "yyyy-MM-dd"
        // LocalDateTime类型，日期和时间部分
        System.out.println(ConverterUtils.formatterDateValue(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss")); // "yyyy-MM-dd HH:mm:ss"
        // java.sql.Date类型，只有日期部分
        System.out.println(ConverterUtils.formatterDateValue(new java.sql.Date(System.currentTimeMillis()), "yyyy-MM-dd")); // "yyyy-MM-dd"
        // java.sql.Date类型，使用日期和时间格式（该类型不包含时间，可能会有问题）
//        System.out.println(ConverterUtils.formatterDateValue(new java.sql.Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss")); // "yyyy-MM-dd 00:00:00"
        // java.util.Date类型，包含日期和时间
        System.out.println(ConverterUtils.formatterDateValue(new java.util.Date(), "yyyy-MM-dd HH:mm:ss")); // "yyyy-MM-dd HH:mm:ss"
        // java.sql.Timestamp类型，日期和时间部分
        System.out.println(ConverterUtils.formatterDateValue(new java.sql.Timestamp(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss")); // "yyyy-MM-dd HH:mm:ss"
        // LocalTime类型，只有时间部分
        System.out.println(ConverterUtils.formatterDateValue(LocalTime.now(), "HH:mm:ss")); // "HH:mm:ss"
        // java.sql.Time类型，只有时间部分
        System.out.println(ConverterUtils.formatterDateValue(new java.sql.Time(System.currentTimeMillis()), "HH:mm:ss")); // "HH:mm:ss"
    }

}