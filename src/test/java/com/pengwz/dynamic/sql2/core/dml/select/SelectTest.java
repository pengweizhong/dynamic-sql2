package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.SqlContext;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Avg;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Count;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Max;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Sum;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonExtract;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonUnquote;
import com.pengwz.dynamic.sql2.core.column.function.logical.CaseWhen;
import com.pengwz.dynamic.sql2.core.column.function.scalar.number.Round;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Md5;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CommonTableExpression;
import com.pengwz.dynamic.sql2.entites.ExamResult;
import com.pengwz.dynamic.sql2.entites.Student;
import com.pengwz.dynamic.sql2.entites.TClass;
import com.pengwz.dynamic.sql2.entites.Teacher;
import com.pengwz.dynamic.sql2.enums.SortOrder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class SelectTest extends InitializingContext {

    SqlContext sqlContext = SqlContext.createSqlContext();

    @Test
    void select1() {
        List<Teacher> list = sqlContext.select()
                .column(Teacher::getTeacherId, "id")
                .column(Teacher::getFirstName)
                .from(Teacher.class)
                .fetch().toList();
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
                .selfJoin("a", on -> on.andEqualTo(Student::getClassId, Student::getClassId))
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
                .column(CaseWhen.builder(Student::getStudentId).build(), "vvv")
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
                        .andEqualTo(new Avg(Student::getLastName),
                                nestedSelect -> nestedSelect.select().column(Student::getStudentId).from(Student.class)))
                .fetch().toList();
        System.out.println(list);
    }

    @Test
    void select8() {
        Student one = sqlContext.select()
                .column(CaseWhen.builder(Student::getStudentId).build())
                .column(Student::getBirthDate)
                .allColumn()
                .from(Student.class)
                .where()
                .exists(nestedSelect -> nestedSelect.select().one().from(Student.class))
                .fetch().toOne();
        System.out.println(one);
    }

    @Test
    void select9() {
        ExamResult one = sqlContext.select()
                //SUM(score) OVER (ORDER BY score DESC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_score
                .column(new Sum(ExamResult::getScore),
                        Over.builder().orderBy(ExamResult::getScore).currentRowToUnboundedFollowing().build(),
                        "aaa")
                .from(ExamResult.class)
                .fetch().toOne();
        System.out.println(one);
    }

    @Test
    void select10() {
        // ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM t_pro_ret_phased), 2) AS percentage
        ExamResult percentage = sqlContext.select()
                .column(new Round(new Count(1).multiply(100).divide(
                        nestedSelect -> {
                            nestedSelect.select().column(new Count(1)).from(Student.class);
                        }), 2), "percentage")
                .from(ExamResult.class)
                .orderByField(">10%", "5~10%", "0~5%", "0%", "<-10%")
                .fetch().toOne();
        System.out.println(percentage);
    }

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
                .from(Student.class)
                .limit(1)
                .fetch(Student.class)
                .toList();
        System.out.println(list);
    }

    @Test
    void select13() {
        CommonTableExpression cte = new CommonTableExpression().with("xxx", nestedSelect ->
                nestedSelect
                        .select()
                        .column(Student::getStudentId)
                        .column(Student::getEnrollmentDate)
                        .from(Student.class));
        sqlContext.select().allColumn().from(cte.cteTable("xxx"))
                .join(cte.cteTable("xxx"), on -> on.andEqualTo(Student::getStudentId, Student::getStudentId));
    }
}






















