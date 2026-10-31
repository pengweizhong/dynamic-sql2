package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.model.Point;

/** ST_Distance_Sphere 距离表达式，支持列、构造函数及其他几何表达式组合。 */
public class DistanceSphere extends BinaryGeometryFunction {
    private String option;
    /** 指定距离单位或球半径，参数有效性由 MySQL 检查。 */
    public DistanceSphere(AbstractColumFunction left, AbstractColumFunction right, double radius) {
        this(left, right);
        option = com.dynamic.sql.utils.SqlUtils.registerValueWithKey(parameterBinder, radius);
    }

    /** 两个任意几何表达式，保留左右操作数的参数绑定顺序。 */
    public DistanceSphere(AbstractColumFunction left, AbstractColumFunction right) {
        super("ST_Distance_Sphere", left, right);
    }

    /** 距离计算必须提供两个操作数，保留旧入口并明确拒绝不完整调用。 */
    @Deprecated
    public DistanceSphere(AbstractColumFunction left) { this(left, missingOperand()); }

    private static AbstractColumFunction missingOperand() {
        throw new IllegalArgumentException("Distance requires two geometry operands");
    }

    /** 实体字段与业务点之间的距离，点值通过 WKT 参数绑定。 */
    public <T,F> DistanceSphere(FieldFn<T,F> field, Point point) {
        this(new Column(field), new GeomFromText(point.toPointString(), point.getSrid()));
    }

    /** 两个业务点之间的距离。 */
    public DistanceSphere(Point left, Point right) {
        this(new GeomFromText(left.toPointString(), left.getSrid()),
                new GeomFromText(right.toPointString(), right.getSrid()));
    }

    @Override public String render(RenderContext context) {
        String sql = super.render(context);
        if (option != null) sql = sql.substring(0, sql.length()-1) + ", " + option + ")";
        return sql.concat(appendArithmeticSql(context));
    }
}
