package com.pengwz.dynamic.sql2.core.column.function.table;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper.OriginColumnAliasImpl;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper.TableAliasImpl;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.TableFunction;

public abstract class AbstractTableFunction implements TableFunction {
    protected TableFunction tableFunction;

    public AbstractTableFunction(TableFunction tableFunction) {
        this.tableFunction = tableFunction;
    }

    public <T, F> AbstractTableFunction(Fn<T, F> field) {
        Fn<T, F> oriFn = field;
        String alias = null;
        if (field instanceof TableAliasImpl) {
            TableAliasImpl abstractAlias = (TableAliasImpl) field;
            oriFn = abstractAlias.getFnColumn();
            alias = abstractAlias.getTableAlias();
        } /*else if (field instanceof OriginColumnAliasImpl) {
            OriginColumnAliasImpl abstractAlias = (OriginColumnAliasImpl) field;
            oriFn = abstractAlias.getFnColumn();
            alias = abstractAlias.getTableAlias();
        }*/
        this.tableFunction = new Column(alias, oriFn);
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return tableFunction.getOriginColumnFn();
    }
}
