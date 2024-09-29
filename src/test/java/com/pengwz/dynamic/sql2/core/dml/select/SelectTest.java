package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Max;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonExtract;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonUnquote;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Md5;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Upper;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CommonTableExpression;
import com.pengwz.dynamic.sql2.entites.*;
import com.pengwz.dynamic.sql2.enums.SortOrder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class SelectTest extends InitializingContext {

    @Test
    void select1() {
        List<Teacher> list = sqlContext.select()
                .column(Teacher::getTeacherId, "id")
                .column(Teacher::getFirstName)
                .column(new Upper(new Md5(Teacher::getFirstName)))
                .column(nestedSelect -> {
                    nestedSelect.select().column(Teacher::getGender).from(Teacher.class).limit(1);
                }, "nested1")
//                .column(new NumberColumn(1))
                .from(Teacher.class)
                .join(Subject.class, on -> on.andEqualTo(Teacher::getTeacherId, Subject::getTeacherId)
                        .orGreaterThan(Teacher::getTeacherId, 1))
                .where(whereCondition -> {
                    whereCondition.andEqualTo(Teacher::getTeacherId, 2);
                    whereCondition.andExists(nestedSelect -> nestedSelect.select().column(Teacher::getGender).from(Teacher.class).limit(1));
                }).groupBy(Teacher::getTeacherId)
                //HAVING COUNT(employee_id) > 5 AND AVG(salary) < 60000;
                .having(havingCondition -> havingCondition.andEqualTo(new Max(Student::getStudentId), 5))
                .limit(1)
                .fetch(Teacher.class)
                .toList();
        System.out.println(list);
    }

    @Test
    void select2() {
        Set<Teacher> set = sqlContext.select()
                .column(Teacher::getTeacherId)
                .column(Teacher::getFirstName)
                .from(Teacher.class)
                .where(
                        condition -> condition.andIsEmpty(Teacher::getTeacherId)
                                .andCondition(c -> c.andIsNull(Teacher::getTeacherId)
                                        .orCondition(d -> d.andLengthEquals(Teacher::getTeacherId, 2)))
                )
                .fetch().toSet();
        System.out.println(set.size());
    }

    @Test
    void select3() {
        List<Student> a = sqlContext.select()
                .allColumn()
                .from(TClass.class)
                .join(Student.class, on -> on.andEqualTo(TClass::getClassId, Student::getClassId))
                .selfJoin(Student.class, "a", on -> on.andEqualTo(Student::getClassId, Student::getClassId))
                .fetch(Student.class).toList();
        System.out.println(a.size());
    }

    @Test
    void select4() {
        HashSet<Student> a = sqlContext.select()
                .allColumn()
                .from(TClass.class)
                .join(Student.class, on -> on.andEqualTo(TClass::getClassId, Student::getClassId))
                .leftJoin(TClass.class, on -> on.andEqualTo(Student::getClassId, TClass::getClassId))
                .where(condition -> condition.andIsNull(Student::getClassId))
                .fetch()
                .toSet(HashSet::new);
        System.out.println(a.size());
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
                .fetch().toMap(Student::getStudentId, Teacher::getTeacherId);
        System.out.println(map);
    }

    @Test
    void select6() {
        List<Student> list = sqlContext.select()
                .column(Student::getStudentId)
                .column(new Md5(new Max(Student::getFirstName)))
                .from(Student.class)
                .where(w -> w.andEqualTo(Student::getLastName, 1))
                .groupBy(Student::getStudentId)
                .having(w -> w.andEqualTo(new Max(Student::getLastName), 2)
                        .andIsNotNull(Student::getStudentId))
                .fetch().toList();
        System.out.println(list);
    }

    @Test
    void select7() {
        List<Student> list = sqlContext.select()
//                .column(CaseWhen.builder(Student::getStudentId).build(), "vvv")
                .column(nestedSelect -> {
                    nestedSelect.select().column(Student::getStudentId).from(Student.class);
                }, "aaa")
                .column(Student::getBirthDate)
                .from(Student.class)
                .where(condition ->
                        condition.andEqualTo(Student::getLastName, nestedSelect -> {
                                    nestedSelect.select().column(Student::getStudentId).from(Student.class);
                                })
                                .orEqualTo(Student::getLastName, "123")
                                .orEqualTo(Student::getLastName, Student::getClassId)
                                .limit(1)
                )
                .groupBy(Student::getStudentId)
                .having(w -> w.andEqualTo(new Max(Student::getLastName), 2)
                        .andEqualTo(new Max(Student::getLastName),
                                nestedSelect -> nestedSelect.select().column(Student::getStudentId).from(Student.class)))
                .fetch().toList();
        System.out.println(list);
    }

//    @Test
//    void select8() {
//        Student one = sqlContext.select()
////                .column(CaseWhen.builder(Student::getStudentId).build())
//                .column(Student::getBirthDate)
//                .allColumn()
//                .from(Student.class)
//                .where()
//                .exists(nestedSelect -> nestedSelect.select().column(new NumberColumn(1)).from(Student.class))
//                .fetch().toOne();
//        System.out.println(one);
//    }

//    @Test
//    void select9() {
//        ExamResult one = sqlContext.select()
//                //SUM(score) OVER (ORDER BY score DESC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_score
//                .column(new Sum(ExamResult::getScore),
//                        Over.builder().orderBy(ExamResult::getScore).currentRowToUnboundedFollowing().build(),
//                        "aaa")
//                .from(ExamResult.class)
//                .fetch().toOne();
//        System.out.println(one);
//    }

//    @Test
//    void select10() {
//        // ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM t_pro_ret_phased), 2) AS percentage
//        ExamResult percentage = sqlContext.select()
//                .column(new Round(new Count(1).multiply(100).divide(
//                        nestedSelect -> {
//                            nestedSelect.select().column(new Count(1)).from(Student.class);
//                        }), 2), "percentage")
//                .from(ExamResult.class)
//                .orderByField(">10%", "5~10%", "0~5%", "0%", "<-10%")
//                .fetch().toOne();
//        System.out.println(percentage);
//    }

    @Test
    void select11() {
        List<Student> list = sqlContext.select()
                .allColumn()
                .from(Student.class)
                .join(TClass.class, on -> on.andEqualTo(Student::getClassId, TClass::getClassId))
                .limit(5, 10)
                .fetch(Student.class)
                .toList();
        System.out.println(list);
    }

    @Test
    void select12() {
        List<Student> list = sqlContext.select()
                .column(new JsonUnquote(new JsonExtract(Student::getLastName, "$.name")))
                .from(Student.class).where(condition ->
                        condition.andEqualTo(Student::getLastName, new JsonExtract(Student::getLastName, "$.name")))
                .limit(1)
                .fetch(Student.class)
                .toList();
        System.out.println(list);
    }

    @Test
    void select13() {
        CommonTableExpression cte = new CommonTableExpression().with(ObjectCTE.class, nestedSelect ->
                nestedSelect
                        .select()
                        .column(Student::getStudentId)
                        .column(Student::getEnrollmentDate)
                        .from(Student.class));
        sqlContext.select().allColumn().from(cte.cteTable(Student.class))
                .join(cte.cteTable(TClass.class), on -> on.andEqualTo(Student::getStudentId, Student::getStudentId));
    }

    @Test
    void select14() {
        sqlContext.select().column(new JsonUnquote(new JsonExtract(Student::getLastName, "$.name"))).from(Student.class);
    }

}






















