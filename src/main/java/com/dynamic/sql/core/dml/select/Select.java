package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.ColumnReference;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;

public class Select {

    private final SelectSpecification selectSpecification = new SelectSpecification();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference(selectSpecification);
    }

    public Select() {
    }

    /**
     * 嵌套查询时调用此参数
     */
    public Select(NestedMeta nestedMeta) {
        selectSpecification.setNestedMeta(nestedMeta);
    }

    public SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }

}
