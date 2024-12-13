package com.dynamic.sql.core.column.function.table;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.AbstractAliasHelper;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.TableFunction;

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
