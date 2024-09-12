package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.SqlContext;
import com.pengwz.dynamic.sql2.entites.Student;
import com.pengwz.dynamic.sql2.entites.TClass;
import com.pengwz.dynamic.sql2.entites.Teacher;
import com.pengwz.dynamic.sql2.enums.SortOrder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;


class SelectTest extends InitializingContext {

    SqlContext sqlContext = SqlContext.createSqlContext();

    @Test
    void select1() {
        sqlContext.select()
                .column(Teacher::getTeacherId, "id")
                .column(Teacher::getFirstName)
                .from(Teacher.class)
                .fetch().toList();
    }

    @Test
    void select2() {
        sqlContext.select()
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
    void select3() {
        sqlContext.select()
                .allColumn()
                .from(TClass.class)
                .join(Student.class, on -> on.andEqualTo(TClass::getClassId, Student::getClassId))
                .selfJoin("a", on -> on.andEqualTo(Student::getClassId, Student::getClassId))
                .fetch().toList();
    }

    @Test
    void select4() {
        sqlContext.select()
                .allColumn()
                .from(TClass.class)
                .join(Student.class, on -> on.andEqualTo(TClass::getClassId, Student::getClassId))
                .selfJoin("a", on -> on.andEqualTo(Student::getClassId, Student::getClassId))
                .where(condition -> condition.andIsNull(Student::getClassId))
                .fetch(Student.class)
                .toSet(HashSet::new);
        ;
    }

    @Test
    void select5() {
        Map<Integer, Integer> map = sqlContext.select()
                .column(Student::getStudentId)
                .column(Teacher::getTeacherId)
                .from(Student.class)
                .where(condition -> condition.andIsNull(Teacher::getTeacherId))
                .groupBy(Teacher::getTeacherId)
                .orderBy(Student::getEnrollmentDate, SortOrder.DESC)
                .thenOrderBy(Student::getBirthDate, SortOrder.ASC)
                .thenOrderBy("id is null desc")
                .fetch().toMap(Student::getStudentId, Teacher::getTeacherId)
//                .fetch().toma
                ;
        System.out.println(map);
    }
}






















