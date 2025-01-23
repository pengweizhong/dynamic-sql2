package com.dynamic.sql;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

public class HelloTest {

    @Test
    void test() {
        System.out.println("Hello World");
        String string = YearMonth.of(2012, 12).toString();
        System.out.println(string);
    }
}

