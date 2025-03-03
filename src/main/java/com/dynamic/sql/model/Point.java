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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, srid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point other = (Point) obj;
        return Double.compare(other.longitude, longitude) == 0 &&
                Double.compare(other.latitude, latitude) == 0 &&
                srid == other.srid;
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
