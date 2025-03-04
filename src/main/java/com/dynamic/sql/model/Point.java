package com.dynamic.sql.model;

import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.utils.WKBUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.nio.ByteOrder;
import java.util.Objects;

public class Point implements Serializable, AttributeConverter<Point, byte[]> {
    private static final long serialVersionUID = 4902022702746614570L;
    //经度（Longitude）表示东西方向的位置，范围是-180到180。
    private double longitude;
    //纬度（Latitude）表示南北方向的位置，范围是-90到90。
    private double latitude;
    //未指定 SRID 时，默认值为0
    private int srid;
    //字节码排序
    private ByteOrder byteOrder;

    // 空构造方法（用于反序列化）
    public Point() {
    }

    // 带经纬度的构造方法
    public Point(double longitude, double latitude) {
        this(longitude, latitude, 0);
    }

    public Point(double longitude, double latitude, int srid) {
        this(longitude, latitude, srid, null);
    }

    public Point(double longitude, double latitude, int srid, ByteOrder byteOrder) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.srid = srid;
        this.byteOrder = byteOrder;
    }

    public Point(@Nonnull Point point) {
        this(point.getLongitude(), point.getLatitude(), point.getSrid(), point.getByteOrder());
    }

    /**
     * 获取经度（Longitude），表示东西方向的位置，范围是-180到180。
     *
     * @return 经度值
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 设置经度（Longitude），表示东西方向的位置，范围是-180到180。
     *
     * @param longitude 经度值
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * 获取纬度（Latitude），表示南北方向的位置，范围是-90到90。
     *
     * @return 纬度值
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 设置纬度（Latitude），表示南北方向的位置，范围是-90到90。
     *
     * @param latitude 纬度值
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取SRID（空间参考系统标识符），用于标识空间参考坐标系统。
     * 默认值为 0，表示没有指定空间参考系统。
     *
     * @return SRID值
     */
    public int getSrid() {
        return srid;
    }

    /**
     * 设置SRID（空间参考系统标识符），用于标识空间参考坐标系统。
     *
     * @param srid SRID值
     */
    public void setSrid(int srid) {
        this.srid = srid;
    }

    /**
     * 获取字节序（Byte Order），表示字节的存储顺序。
     * 可能的值包括大端序（Big Endian）或小端序（Little Endian）。
     *
     * @return 字节序（Byte Order）
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * 设置字节序（Byte Order），用于指定数据的字节存储顺序。
     * 可能的值包括大端序（Big Endian）或小端序（Little Endian）。
     *
     * @param byteOrder 字节序（Byte Order）
     */
    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    /**
     * 以 WKT（Well-Known Text）格式返回点的字符串表示。
     * <p>
     * 格式示例：
     * <pre>
     * POINT (longitude latitude)
     * </pre>
     * 例如：如果经度为 120.1234，纬度为 30.5678，则返回：
     * <pre>
     * POINT (120.1234 30.5678)
     * </pre>
     *
     * @return 该点的 WKT 格式字符串表示
     */
    public String toPointString() {
        return "POINT(" + longitude + " " + latitude + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, srid, byteOrder);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point other = (Point) obj;
        return Double.compare(other.longitude, longitude) == 0 &&
                Double.compare(other.latitude, latitude) == 0 &&
                srid == other.srid &&
                byteOrder == other.byteOrder;
    }

    @Override
    public String toString() {
        return "SRID=" + srid + "; " + toPointString()
                + (byteOrder != null ? "; ByteOrder=" + byteOrder : "");
    }

    @Override
    public byte[] convertToDatabaseColumn(Point point) {
        return WKBUtils.writeWkbBytesFromPoint(point);
    }

    @Override
    public Point convertToEntityAttribute(byte[] dbData) {
        return WKBUtils.readPointFromWkbBytes(dbData);
    }

}
