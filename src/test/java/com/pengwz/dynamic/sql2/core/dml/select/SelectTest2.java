package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.context.SqlContext;
import com.pengwz.dynamic.sql2.context.SqlContextProxy;
import com.pengwz.dynamic.sql2.context.config.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.CrudOperations;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonExtract;
import com.pengwz.dynamic.sql2.core.column.function.json.JsonUnquote;
import com.pengwz.dynamic.sql2.entites.Student;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import org.junit.jupiter.api.Test;


class SelectTest2 {


    @Test
    void select() {
        //强制mysql走oracle语法
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setSqlDialect(SqlDialect.ORACLE);
        CrudOperations sqlContext = SqlContextProxy.newInstance(sqlContextProperties);
        sqlContext.select().column(new JsonUnquote(new JsonExtract(Student::getLastName, "$.name"))).from(Student.class);
    }
}






















