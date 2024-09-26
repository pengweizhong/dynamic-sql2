//package com.pengwz.dynamic.sql2.core.column.function.logical;
//
//import com.pengwz.dynamic.sql2.core.Fn;
//import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
//import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CaseWhen extends ColumnFunctionDecorator {
//
//    private final List<String> conditions = new ArrayList<>();
//    private String elseCondition;
//
//    // 构造方法
//    protected CaseWhen(ColumFunction delegateFunction) {
//        super(delegateFunction);
//    }
//
//    protected <T, F> CaseWhen(Fn<T, F> fn) {
//        super(fn);
//    }
//
//    public static <T, F> CaseWhenBuilder builder(Fn<T, F> fn) {
//        return new CaseWhenBuilder(new CaseWhen(fn));
//    }
//
//    public static CaseWhenBuilder builder(ColumFunction delegateFunction) {
//        return new CaseWhenBuilder(new CaseWhen(delegateFunction));
//    }
//
//    @Override
//    public String getMySqlFunction() {
//        StringBuilder caseExpression = new StringBuilder("case ");
//        for (String condition : conditions) {
//            caseExpression.append(condition).append(" ");
//        }
//        if (elseCondition != null) {
//            caseExpression.append(elseCondition).append(" ");
//        }
//        caseExpression.append("end");
//        return caseExpression.toString();
//    }
//
//    public static class CaseWhenBuilder {
//        private CaseWhen caseWhen;
//
//        public CaseWhenBuilder(CaseWhen caseWhen) {
//            this.caseWhen = caseWhen;
//        }
//
//        public CaseWhen build() {
//            return caseWhen;
//        }
//    }
//
//    // 嵌套条件选择器
//    public static class ConditionalSelector {
//
//    }
//
//}