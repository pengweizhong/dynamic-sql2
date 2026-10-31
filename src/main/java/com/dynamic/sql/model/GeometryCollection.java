package com.dynamic.sql.model;

/** GeometryCollection 标准空间对象，成员类型及 SRID 在构造时检查。 */
public final class GeometryCollection extends Geometry {
    /** 供框架反射初始化属性转换器。 */
    private GeometryCollection() { }
    /** 构造指定 SRID 的集合，允许空集合；成员坐标系必须一致。 */
    public GeometryCollection(int srid, Geometry... members) { super(7,srid,members); }
    /** 查询结果必须符合目标字段声明的几何类型。 */
    @Override public GeometryCollection convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        Geometry geometry=Geometry.read(bytes);
        if (geometry.getGeometryType()!=7) throw new IllegalArgumentException("Expected GeometryCollection geometry");
        GeometryCollection value=new GeometryCollection(); value.copy(geometry); return value;
    }
}
