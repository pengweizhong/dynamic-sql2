package com.dynamic.sql.core.column.function;

/** 构造阶段检查函数参数数量；参数内容及 SQL 类型由数据库按自身语义检查。 */
public final class FunctionArity {
    private FunctionArity() { }
    public static Object[] check(Object[] values,int minimum,int maximum,int step) {
        if(values==null || values.length<minimum || values.length>maximum
                || (step>1 && (values.length-minimum)%step!=0))
            throw new IllegalArgumentException("Invalid function argument count");
        return values.clone();
    }
}
