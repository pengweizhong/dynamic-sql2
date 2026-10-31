package com.dynamic.sql.core.column.function.logical;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import java.util.Objects;

/** 搜索 CASE 等布尔表达式的受控比较操作，操作符不接受外部 SQL 文本。 */
public final class Comparison extends ColumnFunctionDecorator {
    public enum Operator { EQ("="), NE("<>"), GT(">"), GE(">="), LT("<"), LE("<=");
        private final String sql; Operator(String sql) { this.sql=sql; } }
    private final ExpressionArguments args=new ExpressionArguments();
    private final Operator operator;
    public Comparison(Object left,Operator operator,Object right) {
        this.operator=Objects.requireNonNull(operator,"Operator must not be null"); args.add(left);args.add(right);
    }
    @Override public String render(RenderContext context) {
        return "(" + args.render(0,context) + " " + operator.sql + " " + args.render(1,context) + ")";
    }
    @Override public ParameterBinder parameterBinder() { return args.binder(); }
}
