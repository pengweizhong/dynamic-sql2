package com.dynamic.sql.core.dml.select.build.join;


import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;

public class InnerJoin extends JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;
    private Consumer<GenericWhereCondition> onCondition;

    public InnerJoin(Class<?> tableClass, String alias, Consumer<GenericWhereCondition> onCondition) {
        super(alias);
        this.tableClass = tableClass;
        this.onCondition = onCondition;
    }

    public InnerJoin(CteTable cteTable, Consumer<GenericWhereCondition> onCondition) {
        super(null);
        this.cteTable = cteTable;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.INNER;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    public CteTable getCteTable() {
        return cteTable;
    }

    public Consumer<GenericWhereCondition> getOnCondition() {
        return onCondition;
    }
}
