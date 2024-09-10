package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Teacher;
import org.junit.jupiter.api.Test;


class SelectorTest extends InitializingContext {
//    @Test
//    void select1() {
//        List<Teacher> teacherList2 = Selector.instance().allColumn().from(Teacher.class).fetchList();
//        Teacher teacher2 = Selector.instance().allColumn().from(Teacher.class).fetchOne();
//        Selector.instance().allColumn().from(Teacher.class).fetchSet();
//        Selector.instance().allColumn().from(Teacher.class).fetchStream();
//        String s = Selector.instance().allColumn().from(Teacher.class).fetchOne(String.class);
//    }
//
//    @Test
//    public void select2() {
//        List<Teacher> fetchList = Selector.instance()
//                .column(Teacher::getTeacherId)
//                .column(Teacher::getFirstName)
//                .from(Teacher.class).fetchList();
//        System.out.println(fetchList);
//    }

    @Test
    public void select3() {
        Selector.instance()
                .column(Teacher::getTeacherId)
                .column(Teacher::getFirstName)
                .from(Teacher.class).fetchList(Teacher.class);
    }

    @Test
    public void select4() {
        Selector.instance()
                .column(Teacher::getTeacherId)
                .column(Teacher::getFirstName)
                .from(Teacher.class)
                .where(
                        condition -> {
                            condition.andEq(Teacher::getTeacherId, 1)
                                    .orEq(Teacher::getTeacherId, 2);
                        }
                )
                .fetchSet();

    }

}






















