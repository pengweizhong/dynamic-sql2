package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Student;

class DeleteTest extends InitializingContext {


    void delete() {
        sqlContext.deleteByPrimaryKey(Student.class, 1);
    }
}