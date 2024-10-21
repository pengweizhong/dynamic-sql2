package com.pengwz.dynamic.sql2.core.column.function.table;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.TableFunction;

public abstract class AbstractTableFunction implements TableFunction {
    protected TableFunction tableFunction;

    public AbstractTableFunction(TableFunction tableFunction) {
        this.tableFunction = tableFunction;
    }

    public <T, F> AbstractTableFunction(Fn<T, F> field) {
        Fn<T, F> oriFn = field;
        if (field instanceof AbstractAliasHelper) {
            AbstractAliasHelper abstractAlias = (AbstractAliasHelper) field;
            if (abstractAlias.getColumnName() != null) {
                this.tableFunction = new Column(abstractAlias.getTableAlias(), abstractAlias.getColumnName());
            } else {
                this.tableFunction = new Column(abstractAlias.getTableAlias(), abstractAlias.getFnColumn());
            }
        } else {
            this.tableFunction = new Column(null, oriFn);
        }
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return tableFunction.getOriginColumnFn();
    }
}
