package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.anno.Column;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

class ReflectUtilsTest {

    @Test
    void getAllFields() {
        List<Field> allFields = ReflectUtils.getAllFields(Doo.class);
        System.out.println(allFields.size());
        allFields.forEach(System.out::println);
    }

    @Test
    void getAllFields2() {
        List<Field> allFields = ReflectUtils.getAllFields(Coo.class);
        System.out.println(allFields.size());
        allFields.forEach(System.out::println);
    }

}

abstract class Aoo {
    transient int id6;
}

class Boo extends Aoo {
    @Column
    final int id4 = 1;
    //故意设置一个一样的
    int id6;
    static final int id5 = 2;
}

class Coo extends Boo {
    private int id;
    int id2;
    static int id3;
}

class Doo {
    private int id;
    int id2;
    static int id3;
    final int id4 = 1;
    static final int id5 = 2;
    transient int id6;
}