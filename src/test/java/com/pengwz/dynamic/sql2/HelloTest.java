package com.pengwz.dynamic.sql2;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void test() {
        PageHelper.startPage(1, 2).doSelectPageInfo(() -> System.getenv());
        PageMethod.startPage(1,1).doSelectPage(() -> System.getenv());
        System.out.println("Hello World");
    }
}

