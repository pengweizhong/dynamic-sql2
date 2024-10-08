package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Max;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonExtract;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonUnquote;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Md5;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Upper;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CommonTableExpression;
import com.pengwz.dynamic.sql2.entites.*;
import com.pengwz.dynamic.sql2.enums.SortOrder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pengwz.dynamic.sql2.core.column.conventional.AbstractAlias.withTableAlias;


class SelectTest extends InitializingContext {

    @Test
    void select1() {
        List<Teacher> list = sqlContext.select()
                .column(Teacher::getTeacherId, "id")
                .column(Teacher::getFirstName)
                .column("t3", Subject::getSubjectName)
                .column(new Upper(new Md5("t1", Teacher::getFirstName)), "")
                .column(new Max(Student::getBirthDate), Over.builder().build(), "aa")
                .column(nestedSelect -> {
                    nestedSelect.select().column(Teacher::getGender).from(Teacher.class).limit(1);
                }, "nested1")
//                .column(new NumberColumn(1))
                .from(Teacher.class, "t0")
                .join(Subject.class, "t2", on -> on.andEqualTo(Teacher::getTeacherId, Subject::getTeacherId)
                        .orGreaterThan(Teacher::getTeacherId, 1))
                .leftJoin(TClass.class, condition -> condition.andEqualTo(TClass::getClassTeacherId, Teacher::getTeacherId))
                .rightJoin(Subject.class, "t3", on ->
                        on.andEqualTo(withTableAlias("t3", Subject::getTeacherId), withTableAlias("t1", Subject::getTeacherId)))
                .where(whereCondition -> {
                    whereCondition.andNotEqualTo(Teacher::getTeacherId, -1);
                    whereCondition.andExists(nested -> nested.select().column(Teacher::getGender).from(Teacher.class).limit(1));
                    whereCondition.andCondition(condition ->
                            condition.andEqualTo(withTableAlias("t3", Teacher::getGender), Student.GenderEnum.Male)
                                    .orEqualTo(Teacher::getGender, Student.GenderEnum.Female));
                    whereCondition.orCondition(condition ->
                            condition.andEqualTo(Teacher::getGender, Student.GenderEnum.Male)
                                    .orEqualTo(Teacher::getGender, Student.GenderEnum.Female));
                })
                .groupBy(Teacher::getTeacherId)
//                .groupBy(withTableAlias("123",Teacher::getTeacherId))
                .having(havingCondition -> havingCondition.andEqualTo(new Max(withTableAlias("t1", Teacher::getBirthDate)), 5))
//                .orderBy(Teacher::getTeacherId)
                .orderBy(withTableAlias("123",Teacher::getTeacherId))
                .thenOrderBy(Teacher::getBirthDate, SortOrder.DESC)
                .limit(0, 10)
                .fetch(Teacher.class)
                .toList();
        System.out.println(list);
    }

    @Test
    void select1_1() {
        sqlContext.select()
                .column(Teacher::getTeacherId)
                .column(Teacher::getFirstName)
                .column(Subject::getSubjectName)
                .from(Teacher.class, "t1")
                .fullJoin(TClass.class, "t2", on -> on.andEqualTo(Teacher::getTeacherId, TClass::getClassTeacherId))
                .innerJoin(Subject.class, "t3", on -> on.andEqualTo(Teacher::getTeacherId, Subject::getSubjectId))
                .fetch(Teacher.class)
                .toList();
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






















