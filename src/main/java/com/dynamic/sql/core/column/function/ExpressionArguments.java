package com.dynamic.sql.core.column.function;

import com.dynamic.sql.core.placeholder.ParameterBinder;
import java.util.ArrayList;
import java.util.List;

/** 通用有序操作数容器，表达式保持结构，其他值转换为绑定常量。 */
public final class ExpressionArguments {
    private final List<ColumFunction> values=new ArrayList<>();
    public int add(Object value) {
        values.add(value instanceof ColumFunction ? (ColumFunction)value : new Value(value));
        return values.size()-1;
    }
    public String render(int index,RenderContext context) { return values.get(index).render(context); }
    public int size() { return values.size(); }
    public ParameterBinder binder() {
        ParameterBinder result=new ParameterBinder();
        for (ColumFunction value:values) result.addParameterBinder(value.parameterBinder());
        return result;
    }
}
