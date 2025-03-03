package com.dynamic.sql.model;

import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.utils.WKBUtils;

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

    // 复制构造方法
    public Point(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("point is null");
        }
        this.longitude = point.longitude;
        this.latitude = point.latitude;
        this.srid = point.srid;
        this.byteOrder = point.byteOrder;
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
     * 将当前 Point 对象转换为 JSON 格式字符串（使用字符串拼接）。
     *
     * @return JSON 字符串表示
     */
    public String toJSONString() {
        // 使用 StringBuilder 拼接 JSON 字符串
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"longitude\": ").append(longitude).append(", ");
        json.append("\"latitude\": ").append(latitude).append(", ");
        json.append("\"srid\": ").append(srid).append(", ");
        json.append("\"byteOrder\": \"").append(byteOrder != null ? byteOrder.toString() : "").append("\"");
        json.append("}");
        return json.toString();
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
        return "SRID=" + srid + "; POINT (" + longitude + " " + latitude + ")"
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
