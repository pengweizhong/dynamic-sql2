package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.SqlContext;
import com.pengwz.dynamic.sql2.entites.Student;

class DeleteTest extends InitializingContext {
    SqlContext sqlContext = SqlContext.createSqlContext();

    void delete() {
        sqlContext.deleteByPrimaryKey(Student.class, 1);
    }
}