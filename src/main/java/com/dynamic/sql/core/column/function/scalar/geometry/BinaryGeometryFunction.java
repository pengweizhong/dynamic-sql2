package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import java.util.Objects;

/** 双几何函数公共结构，左右操作数均可为列或嵌套表达式，按 SQL 顺序合并参数。 */
abstract class BinaryGeometryFunction extends ColumnFunctionDecorator implements ScalarFunction {
    private final AbstractColumFunction right;
    private final String functionName;

    /** 构造时检查必需操作数，避免渲染时出现无上下文的空指针。 */
    protected BinaryGeometryFunction(String name, AbstractColumFunction left, AbstractColumFunction right) {
        super(Objects.requireNonNull(left, "Left geometry must not be null"));
        this.right = Objects.requireNonNull(right, "Right geometry must not be null");
        this.functionName = name;
    }

    @Override public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException(functionName, context.getSqlDialect());
        return functionName + "(" + delegateFunction.render(context) + ", " + right.render(context) + ")";
    }

    /** 每次返回独立参数容器，避免重复读取绑定信息时不断追加参数。 */
    @Override public ParameterBinder parameterBinder() {
        return new ParameterBinder().addParameterBinder(delegateFunction.parameterBinder())
                .addParameterBinder(right.parameterBinder()).addParameterBinder(parameterBinder);
    }
}
