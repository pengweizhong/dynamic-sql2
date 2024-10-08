//package com.pengwz.dynamic.sql2.core.column.function.aggregate;
//
//import com.pengwz.dynamic.sql2.core.Fn;
//import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
//import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
//import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
//import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
//
//public class Sum extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {
//
//    public Sum(ColumFunction delegateFunction) {
//        super(delegateFunction);
//    }
//
//    public <T, F> Sum(Fn<T, F> fn) {
//        super(fn);
//    }
//
//    @Override
//    public String apply(Over over) {
//        //
////        String sql = windowsFunction.apply(over) + " OVER (" +
////                partitionByClause + " ORDER BY " + orderByClause +
////                " " + frameSpecification + ") AS " + alias;
//        return "";
//    }
//
//    @Override
//    public String getMySqlFunction() {
//        return "sum(" + delegateFunction.getMySqlFunction() + ")";
//    }
//
//    @Override
//    public String getOracleFunction() {
//        return "SUM(" + delegateFunction.getOracleFunction() + ")";
//    }
//}
