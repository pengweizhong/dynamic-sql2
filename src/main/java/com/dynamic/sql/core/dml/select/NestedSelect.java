package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.dml.select.build.SelectSpecification;

public class NestedSelect {
   Select select = new Select();

    public SelectSpecification getSelectSpecification() {
        return select.getSelectSpecification();
    }
}
