package com.dynamic.sql.model;

/** MultiPoint 标准空间对象，成员类型及 SRID 在构造时检查。 */
public final class MultiPoint extends Geometry {
    /** 供框架反射初始化属性转换器。 */
    private MultiPoint() { }
    /** 构造指定 SRID 的集合，允许空集合；成员坐标系必须一致。 */
    public MultiPoint(int srid, Point... members) { super(4,srid,convert(members)); }
    /** 查询结果必须符合目标字段声明的几何类型。 */
    @Override public MultiPoint convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        Geometry geometry=Geometry.read(bytes);
        if (geometry.getGeometryType()!=4) throw new IllegalArgumentException("Expected MultiPoint geometry");
        MultiPoint value=new MultiPoint(); value.copy(geometry); return value;
    }
    /** 将标准成员模型转换为通用结构，构造过程不暴露内部二进制。 */
    private static Geometry[] convert(Point[] members) {
        Geometry[] result=new Geometry[members.length];
        for (int i=0;i<members.length;i++) result[i]=new Geometry(members[i]);
        return result;
    }
}
