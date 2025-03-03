package com.dynamic.sql.model;

import com.dynamic.sql.plugins.conversion.AttributeConverter;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class Point implements Serializable, AttributeConverter<Point, byte[]> {
    private static final long serialVersionUID = 4902022702746614570L;
    //经度（Longitude）表示东西方向的位置，范围是-180到180。
    private double longitude;
    // 纬度（Latitude）表示南北方向的位置，范围是-90到90。
    private double latitude;
    //未指定 SRID 时，默认值为0
    private int srid;

    // 空构造方法（用于反序列化）
    public Point() {
    }

    // 带经纬度的构造方法
    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Point(double longitude, double latitude, int srid) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.srid = srid;
    }

    // 复制构造方法
    public Point(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("point is null");
        }
        this.longitude = point.longitude;
        this.latitude = point.latitude;
        this.srid = point.srid;
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
        return "SRID=" + srid + "; POINT (" + getLongitude() + " " + getLatitude() + ")";
    }

    @Override
    public byte[] convertToDatabaseColumn(Point point) {
        System.out.println("********************************************* convertToEntityAttribute");
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putDouble(point.getLongitude());
        buffer.putDouble(point.getLatitude());
        return buffer.array();
    }

    @Override
    public Point convertToEntityAttribute(byte[] dbData) {
        System.out.println("Received byte[] data: " + Arrays.toString(dbData)); // 打印原始 byte[] 数据
        System.out.println("Converted Point: Longitude = " + longitude + ", Latitude = " + latitude);
        Point point = parsePointData(dbData);
        System.out.println(point);
        return point;
    }

    private static Point parsePointData(byte[] pointData) {
        System.out.println("Debug: pointData bytes = " + java.util.Arrays.toString(pointData)); //打印byte数组

        if (pointData.length == 25) {
            boolean littleEndian = pointData[0] == 1;
            int wkbType = littleEndianToInt(pointData, 1);

            if (wkbType == 1) {
                double longitude = bytesToDouble(pointData, 5, littleEndian);
                double latitude = bytesToDouble(pointData, 13, littleEndian);
                return new Point(latitude, longitude);
            } else {
                System.out.println("Error: WKB type mismatch (no SRID).");
                return null;
            }
        } else if (pointData.length >= 29) {
            // 包含SRID的情况
            boolean littleEndian = pointData[0] == 1;
            int wkbType = littleEndianToInt(pointData, 1);
            int srid = littleEndianToInt(pointData, 5);

            if (wkbType == 1 && srid == 4326) {
                double longitude = bytesToDouble(pointData, 9, littleEndian);
                double latitude = bytesToDouble(pointData, 17, littleEndian);
                return new Point(latitude, longitude);
            } else {
                System.out.println("Error: WKB type or SRID mismatch.");
                return null;
            }
        } else {
            System.out.println("Error: pointData length is invalid. Length: " + pointData.length);
            return null;
        }
    }

    private static int littleEndianToInt(byte[] bytes, int offset) {
        int result = (bytes[offset] & 0xFF) |
                ((bytes[offset + 1] & 0xFF) << 8) |
                ((bytes[offset + 2] & 0xFF) << 16) |
                ((bytes[offset + 3] & 0xFF) << 24);

        System.out.println("Debug: littleEndianToInt(" + java.util.Arrays.toString(bytes) + ", " + offset + ") = " + result); // 添加调试信息
        return result;
    }


    private static double bytesToDouble(byte[] bytes, int offset, boolean littleEndian) {
        long value = 0;
        if (littleEndian) {
            for (int i = 0; i < 8; i++) {
                value |= ((long) bytes[offset + i] & 0xFF) << (i * 8);
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                value |= ((long) bytes[offset + i] & 0xFF) << ((7 - i) * 8);
            }
        }
        return Double.longBitsToDouble(value);
    }


}
