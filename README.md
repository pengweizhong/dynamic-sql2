# Dynamic-SQL

（正在逐步实现）Dynamic-SQL是一个高扩展性的 ORM 框架，支持自动化数据库差异处理和动态数据源切换。  
框架具备智能查询构建器，可以自动生成适配 MySQL、Oracle、PostgreSQL、SQL Server 等数据库的 SQL。  
支持复杂查询、多数据源管理、分布式事务和自动模式迁移。设计上采用领域驱动设计和领域特定语言（DSL），  
并提供缓存和异步反应式支持，旨在简化数据库操作并提高开发效率。未来将继续扩展对新数据库的支持。

# 设计原则

1. 自动化数据库差异处理：  
   • 方言机制：具备强大的方言处理能力，根据不同数据库自动生成合适的 SQL。支持 MySQL、Oracle 等常见数据库。  
   • 自动查询生成：对增删改查操作尽可能由框架自动生成 SQL，屏蔽数据库之间的差异。  
2. 动态数据源切换：  
   • 提供灵活的数据源管理能力，能够根据业务需求动态切换不同的数据源，并确保事务性和一致性。  
   • 多数据源支持：动态切换或平滑切换数据源，支持跨数据库的操作和事务管理，切换数据源而无需修改SQL，有效降低迁移成本。  
3. 高扩展性：  
   • 灵活扩展特定数据库的功能，尤其是在一些数据库特有的功能（如存储过程、序列、窗口函数、自定义函数）上，提供钩子或扩展点。  
   • 模块化设计，能够方便地接入新数据库类型或者定制新的方言规则。  
4. 智能查询构建器：  
   • 提供类似 QueryDSL 或 JPA Criteria 的查询构建器，开发者可以通过类型安全的方式构建查询，无需手写 SQL。  
   • 查询构建器允许嵌套条件、函数、动态条件、分页、排序、分组等复杂操作，同时对数据库差异（如分页、表连接）自动处理。  
5. 事务管理和分布式事务：  
   • 支持单一数据库的事务管理，同时针对多数据源的操作引入分布式事务支持。  
   • 自定义分布式事务管理器，确保在多个数据库之间操作时数据的一致性。   


# 设计细节

1. 领域驱动设计  
   采用 DDD（领域驱动设计）的思路，将业务逻辑、数据模型和持久化层紧密结合，  
   通过聚合根、实体、值对象等方式减少开发者在数据库访问上的认知负担。
2. 领域特定语言（DSL）  
   提供一套清晰简洁的 DSL，开发者可以通过类 SQL 语法构建查询，而不需要关心底层数据库实现。  
3. 缓存支持  
   提供二级实体缓存、查询缓存等功能，减少数据库访问压力。  
4. 异步和反应式支持    
   随着微服务和响应式架构的流行，框架具备异步和反应式数据库访问的能力，  
   支持基于 CompletableFuture 或 Reactor的非阻塞查询。

# 应对业务场景

1. 标准增删改查：支持基本的 CRUD 操作，且能适配不同的数据库。  
2. 复杂关联查询：支持复杂的表关联、嵌套查询等场景。  
3. 数据分页和排序：内置分页、排序功能，自动适配不同数据库的分页机制（如 MySQL 的 LIMIT、Oracle 的 ROWNUM 等）。  
4. 历史数据或审计功能：可以内置审计功能，自动记录实体的创建时间、更新时间、操作者等。  
5. 兼容多种数据库特性：支持数据库特有的功能（如 Oracle 的 sequence，PostgreSQL 的  
   jsonb），而且可以为特定数据库实现定制化的功能。  

# 未来扩展和维护

• 支持新的数据库：通过方言机制可以灵活地引入对新数据库的支持。  
• 框架插件化：轻松为其添加新功能或适配新的数据库。    

# 目前能看到的一些API的调用效果

> 这个仅仅体现API的流式调用，不关心业务的合理问题（比如 `join` 了毫不相干的表）  

```java
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
                .fetch(Student.class)
                .toList();
        System.out.println(list);
    }
}
```
# 开源支持

感谢 [JetBrains](https://www.jetbrains.com/) 提供的开源许可证支持，使我能够更好地开发和维护本项目。
  
![jetbrains.png](jetbrains.png)
