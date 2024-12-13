package com.dynamic.sql.utils;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import com.dynamic.sql.enums.GenerationType;
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

@Table("boo")
class Boo extends Aoo {
    @Column
    final int id4 = 1;
    //故意设置一个一样的
    @Column(primary = true)
    int id6;
    static final int id5 = 2;
}

@Table("coo")
class Coo extends Boo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,sequenceName = "hello SEQ")
    private int id;
    int id2;
    static int id3;
}

@Table("coo")
class Doo {
    private int id;
    int id2;
    static int id3;
    final int id4 = 1;
    static final int id5 = 2;
    transient int id6;
}