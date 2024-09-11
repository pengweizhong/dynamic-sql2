package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Student;
import com.pengwz.dynamic.sql2.entites.TClass;
import com.pengwz.dynamic.sql2.entites.Teacher;
import org.junit.jupiter.api.Test;


class SelectorTest extends InitializingContext {


    @Test
    public void select1() {
        Selector.instance()
                .column(Teacher::getTeacherId)
                .column(Teacher::getFirstName).as("name")
                .from(Teacher.class)
                .fetch().toList();
    }

    @Test
    public void select2() {
        Selector.instance()
                .column(Teacher::getTeacherId)
                .column(Teacher::getFirstName)
                .from(Teacher.class)
                .where(
                        condition -> condition.andIsEmpty(Teacher::getTeacherId)
                                .andCondition(c -> c.andIsNull(Teacher::getTeacherId)
                                        .orCondition(d -> d.andLengthEquals(Teacher::getTeacherId, 2)))
                )
                .fetch(Teacher.class).toSet();

    }

    @Test
    public void select3() {
        Selector.instance()
                .allColumn()
                .from(TClass.class)
                .join(Student.class, on -> on.andEqualTo(TClass::getClassId, Student::getClassId))
                .selfJoin("a", on -> on.andEqualTo(Student::getClassId, Student::getClassId))
                .fetch().toList();
    }

    @Test
    public void select4() {
        Selector.instance()
                .allColumn()
                .from(TClass.class)
                .join(Student.class, on -> on.andEqualTo(TClass::getClassId, Student::getClassId))
                .selfJoin("a", on -> on.andEqualTo(Student::getClassId, Student::getClassId))
                .where(condition -> condition.andIsNull(Student::getClassId))
                .fetch(Student.class).toSet();
    }
}






















