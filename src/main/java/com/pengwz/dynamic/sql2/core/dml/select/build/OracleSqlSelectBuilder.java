package com.pengwz.dynamic.sql2.core.dml.select.build;

public class OracleSqlSelectBuilder extends GenericSqlSelectBuilder {

    public OracleSqlSelectBuilder(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    @Override
    public void parseLimit() {
        //Oracle需要单独处理limit
        super.parseLimit();
    }
}
