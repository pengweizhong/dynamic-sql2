//package com.dynamic.sql.core.column.function.logical;
//
//
//import com.dynamic.sql.core.column.function.AbstractColumFunction;
//import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
//import com.dynamic.sql.core.column.function.RenderContext;
//import com.dynamic.sql.core.condition.WhereCondition;
//
//import java.util.function.Consumer;
//
////TODO 需要时再实现
//public class CaseWhen extends ColumnFunctionDecorator {
//
//    public CaseWhen(AbstractColumFunction delegateFunction) {
//        super(delegateFunction);
//    }
//
//    public CaseWhen(Consumer<WhereCondition> condition) {
//
//    }
//
//    public CaseWhenBuilder else_(Object elseValue) {
//        return null;
//    }
//
//    public CaseWhenBuilder otherwise(Object elseValue) {
//        return else_(elseValue);
//    }
//
//
//    @Override
//    public String render(RenderContext context) {
//        return "";
//    }
//
//    public static class CaseWhenBuilder {
//        private CaseWhen caseWhen;
//
//
//        public CaseWhen build() {
//            return caseWhen;
//        }
//
//    }
//}