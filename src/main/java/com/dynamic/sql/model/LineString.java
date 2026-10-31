package com.dynamic.sql.model;

import com.dynamic.sql.plugins.conversion.AttributeConverter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/** 线串值对象，沿用 Point 的坐标与 SRID 语义；转换器处理 MySQL 带 SRID 的内部空间二进制。 */
public final class LineString implements AttributeConverter<LineString, byte[]>, Iterable<Point>, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Point> points;
    private final int srid;
    private final ByteOrder byteOrder;

    /** 供框架反射创建转换器，转换方法不依赖当前实例的坐标。 */
    private LineString() { points = Collections.emptyList(); srid = 0; byteOrder = ByteOrder.LITTLE_ENDIAN; }

    /** 线串至少包含两个点，不自动闭合；元数据取自第一个点。 */
    public LineString(Point first, Point second, Point... others) {
        this(join(first, second, others));
    }

    /** 深拷贝输入点，拒绝混合 SRID 和非有限坐标，防止构造后的值被外部修改。 */
    public LineString(List<Point> source) {
        if (source == null || source.size() < 2) throw new IllegalArgumentException("LineString requires at least two points");
        Point first = Objects.requireNonNull(source.get(0), "Point must not be null");
        srid = first.getSrid();
        byteOrder = first.getByteOrder() == null ? ByteOrder.LITTLE_ENDIAN : first.getByteOrder();
        List<Point> copy = new ArrayList<>(source.size());
        for (Point point : source) {
            Objects.requireNonNull(point, "Point must not be null");
            if (point.getSrid() != srid) throw new IllegalArgumentException("LineString points must use the same SRID");
            if (!Double.isFinite(point.getLongitude()) || !Double.isFinite(point.getLatitude()))
                throw new IllegalArgumentException("LineString coordinates must be finite");
            copy.add(new Point(point.getLongitude(), point.getLatitude(), srid, byteOrder));
        }
        points = Collections.unmodifiableList(copy);
    }

    /** 创建统一的构造参数集合。 */
    private static List<Point> join(Point first, Point second, Point[] others) {
        List<Point> result = new ArrayList<>(Arrays.asList(first, second));
        if (others != null) Collections.addAll(result, others);
        return result;
    }

    /** 返回点的防御性副本，避免通过可变 Point 修改内部值。 */
    public List<Point> getPoints() {
        List<Point> result = new ArrayList<>(points.size());
        for (Point point : points) result.add(new Point(point));
        return Collections.unmodifiableList(result);
    }
    public int getSrid() { return srid; }
    public ByteOrder getByteOrder() { return byteOrder; }
    @Override public Iterator<Point> iterator() { return getPoints().iterator(); }

    /** SRID 头固定小端，WKB 载荷按线串字节序编码，不与标准 WKB 混用。 */
    @Override public byte[] convertToDatabaseColumn(LineString line) {
        if (line == null) return null;
        int size = Math.addExact(13, Math.multiplyExact(line.points.size(),16));
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(line.srid);
        buffer.put((byte)(line.byteOrder == ByteOrder.LITTLE_ENDIAN ? 1 : 0)).order(line.byteOrder);
        buffer.putInt(2).putInt(line.points.size());
        for (Point point : line.points) buffer.putDouble(point.getLongitude()).putDouble(point.getLatitude());
        return buffer.array();
    }

    /** 分配点集合前严格校验标记、类型、点数和载荷长度，拒绝截断或尾随垃圾。 */
    @Override public LineString convertToEntityAttribute(byte[] bytes) {
        if (bytes == null) return null;
        if (bytes.length < 13) throw new IllegalArgumentException("Invalid MySQL LineString payload");
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int readSrid = buffer.getInt();
        int marker = Byte.toUnsignedInt(buffer.get());
        if (marker != 0 && marker != 1) throw new IllegalArgumentException("Invalid WKB byte order");
        ByteOrder order = marker == 1 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        buffer.order(order);
        if (buffer.getInt() != 2) throw new IllegalArgumentException("Expected WKB LineString type");
        int count = buffer.getInt();
        if (count < 2 || count != buffer.remaining()/16 || buffer.remaining()%16 != 0)
            throw new IllegalArgumentException("Invalid LineString point count or length");
        List<Point> result = new ArrayList<>(count);
        for (int i=0;i<count;i++) result.add(new Point(buffer.getDouble(),buffer.getDouble(),readSrid,order));
        return new LineString(result);
    }

    /** 几何值相等由坐标和 SRID 决定，编码字节序不改变几何值。 */
    @Override public boolean equals(Object value) {
        if (this == value) return true;
        if (!(value instanceof LineString)) return false;
        LineString other = (LineString)value;
        if (srid != other.srid || points.size() != other.points.size()) return false;
        for (int i=0;i<points.size();i++)
            if (Double.compare(points.get(i).getLongitude(),other.points.get(i).getLongitude()) != 0
                    || Double.compare(points.get(i).getLatitude(),other.points.get(i).getLatitude()) != 0) return false;
        return true;
    }
    @Override public int hashCode() {
        int hash = srid;
        for (Point point : points) hash = 31*hash + Objects.hash(point.getLongitude(),point.getLatitude());
        return hash;
    }
    @Override public String toString() {
        StringBuilder result = new StringBuilder("LINESTRING(");
        for (int i=0;i<points.size();i++) {
            if (i>0) result.append(",");
            result.append(points.get(i).getLongitude()).append(" ").append(points.get(i).getLatitude());
        }
        return result.append(")").toString();
    }
}
