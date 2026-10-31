package com.dynamic.sql.core.column.function.logical;

import com.dynamic.sql.core.column.function.FunctionExpression;

/** COALESCE 条件表达式，值参数可为列、嵌套表达式或绑定常量。 */
public final class Coalesce extends FunctionExpression {
    public Coalesce(Object first, Object second, Object... others) { super("COALESCE",merge(first,second,others)); }
    private static Object[] merge(Object first,Object second,Object[] others) {
        Object[] values=new Object[others.length+2]; values[0]=first; values[1]=second;
        System.arraycopy(others,0,values,2,others.length); return values;
    }
}
