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
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putDouble(point.getLongitude());
        buffer.putDouble(point.getLatitude());
        return buffer.array();
    }

    @Override
    public Point convertToEntityAttribute(byte[] dbData) {
        return readCoordinateFromWkbBytes(dbData);
    }

    /**
     * 从 WKB（Well-Known Binary）字节数组中读取坐标数据，返回一个 {@link Point} 对象。
     * 该方法根据字节数组的内容反序列化经度和纬度，并将它们封装为 {@link Point} 对象。
     *
     * @param wkbBytes 长度为 25 字节的 WKB 数据，包含经纬度信息。
     * @return 包含经度和纬度信息的 {@link Point} 对象。
     */
    public static Point readCoordinateFromWkbBytes(byte[] wkbBytes) {
        // 判断字节顺序，这里假设是小端字节序
        boolean isBigEndian = readIsWkbBigEndianByteOrder(wkbBytes[0]);
        // 读取经度和纬度，分别位于字节数组的 9 和 17 位置
        double x = readDoubleFromBytes(wkbBytes, 9, isBigEndian);
        double y = readDoubleFromBytes(wkbBytes, 17, isBigEndian);
        // 创建 Point 对象并设置经纬度
        Point coordinate = new Point();
        coordinate.setLongitude(x);
        coordinate.setLatitude(y);
        return coordinate;
    }

    /**
     * 判断字节是否表示大端字节序。
     *
     * @param b 字节值，表示字节序标识。
     * @return 如果字节值为 0，则为大端字节序；否则为小端字节序。
     */
    public static boolean readIsWkbBigEndianByteOrder(byte b) {
        final byte BIG_ENDIAN = 0;
        final byte LITTLE_ENDIAN = 1;
        // 如果字节值为 BIG_ENDIAN（0），表示大端字节序
        return b == BIG_ENDIAN;
    }

    /**
     * 从字节数组中读取一个双精度浮点数（double），支持大端和小端字节序。
     *
     * @param buf       字节数组，必须至少包含 8 个字节。
     * @param offset    偏移量，表示从字节数组中的哪个位置开始读取。
     * @param bigEndian 如果为 true，表示采用大端字节序；如果为 false，表示采用小端字节序。
     * @return 反序列化后的双精度浮点数（double）。
     */
    public static double readDoubleFromBytes(byte[] buf, int offset, boolean bigEndian) {
        // 从字节数组中获取 8 字节数据并转换为 double 值
        byte[] bufOf8Bytes = Arrays.copyOfRange(buf, offset, offset + 8);
        return readDoubleFromBytes(bufOf8Bytes, bigEndian);
    }

    /**
     * 从字节数组中读取一个双精度浮点数（double），支持大端和小端字节序。
     *
     * @param buf       字节数组，必须至少包含 8 个字节。
     * @param bigEndian 如果为 true，表示采用大端字节序；如果为 false，表示采用小端字节序。
     * @return 反序列化后的双精度浮点数（double）。
     */
    public static double readDoubleFromBytes(byte[] buf, boolean bigEndian) {
        // 根据字节序读取 8 字节数据并转换为 long 类型
        long longVal = bigEndian ? readLongFromBytesBigEndian(buf)
                : readLongFromBytesLittleEndian(buf);
        // 将 long 值转换为 double 类型
        return Double.longBitsToDouble(longVal);
    }

    /**
     * 从字节数组（大端字节序）中读取一个长整型数值。
     *
     * @param buf 字节数组，必须包含 8 个字节。
     * @return 反序列化后的长整型数值。
     */
    public static long readLongFromBytesBigEndian(byte[] buf) {
        return (long) (buf[0] & 0xff) << 56
                | (long) (buf[1] & 0xff) << 48
                | (long) (buf[2] & 0xff) << 40
                | (long) (buf[3] & 0xff) << 32
                | (long) (buf[4] & 0xff) << 24
                | (long) (buf[5] & 0xff) << 16
                | (long) (buf[6] & 0xff) << 8
                | (long) (buf[7] & 0xff);
    }

    /**
     * 从字节数组（小端字节序）中读取一个长整型数值。
     *
     * @param buf 字节数组，必须包含 8 个字节。
     * @return 反序列化后的长整型数值。
     */
    public static long readLongFromBytesLittleEndian(byte[] buf) {
        return (long) (buf[7] & 0xff) << 56
                | (long) (buf[6] & 0xff) << 48
                | (long) (buf[5] & 0xff) << 40
                | (long) (buf[4] & 0xff) << 32
                | (long) (buf[3] & 0xff) << 24
                | (long) (buf[2] & 0xff) << 16
                | (long) (buf[1] & 0xff) << 8
                | (long) (buf[0] & 0xff);
    }

}
