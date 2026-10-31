package com.dynamic.sql.core.column.function.logical;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import java.util.ArrayList;
import java.util.List;

/** 支持搜索 CASE 与简单 CASE，所有分支常量通过统一参数绑定。 */
public final class CaseWhen extends ColumnFunctionDecorator {
    private final ExpressionArguments args=new ExpressionArguments();
    private final Integer operand;
    private final List<int[]> branches=new ArrayList<>();
    private Integer otherwise;
    public CaseWhen() { operand=null; }
    public CaseWhen(Object operand) { this.operand=args.add(operand); }
    public CaseWhen when(Object condition,Object value) {
        if(operand==null && !(condition instanceof ColumFunction))
            throw new IllegalArgumentException("Searched CASE requires a condition expression");
        branches.add(new int[]{args.add(condition),args.add(value)});return this;
    }
    public CaseWhen otherwise(Object value) {
        if(otherwise!=null) throw new IllegalStateException("CASE ELSE already specified");
        otherwise=args.add(value);return this;
    }
    public CaseWhen else_(Object value) { return otherwise(value); }
    @Override public String render(RenderContext context) {
        if(branches.isEmpty()) throw new IllegalStateException("CASE requires at least one WHEN");
        StringBuilder sql=new StringBuilder("CASE");
        if(operand!=null) sql.append(" ").append(args.render(operand,context));
        for(int[] branch:branches) sql.append(" WHEN ").append(args.render(branch[0],context))
                .append(" THEN ").append(args.render(branch[1],context));
        if(otherwise!=null) sql.append(" ELSE ").append(args.render(otherwise,context));
        return sql.append(" END").append(appendArithmeticSql(context)).toString();
    }
    @Override public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
