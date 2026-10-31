package com.dynamic.sql.model;

/** MultiLineString 标准空间对象，成员类型及 SRID 在构造时检查。 */
public final class MultiLineString extends Geometry {
    /** 供框架反射初始化属性转换器。 */
    private MultiLineString() { }
    /** 构造指定 SRID 的集合，允许空集合；成员坐标系必须一致。 */
    public MultiLineString(int srid, LineString... members) { super(5,srid,convert(members)); }
    /** 查询结果必须符合目标字段声明的几何类型。 */
    @Override public MultiLineString convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        Geometry geometry=Geometry.read(bytes);
        if (geometry.getGeometryType()!=5) throw new IllegalArgumentException("Expected MultiLineString geometry");
        MultiLineString value=new MultiLineString(); value.copy(geometry); return value;
    }
    /** 将标准成员模型转换为通用结构，构造过程不暴露内部二进制。 */
    private static Geometry[] convert(LineString[] members) {
        Geometry[] result=new Geometry[members.length];
        for (int i=0;i<members.length;i++) result[i]=new Geometry(members[i]);
        return result;
    }
}
