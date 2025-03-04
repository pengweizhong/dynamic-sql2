package com.dynamic.sql.utils;

import com.dynamic.sql.model.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * WKB 是用于存储几何数据的二进制格式，在地理信息系统（GIS）和数据库（如 PostGIS）中广泛使用。
 * <p>
 * WKB 结构：
 * <pre>
 *
 * ┌───────────┬───────────┬───────────┬────────────┬────────────┐
 * │   SRID    │  字节序    │  几何类型  │   X 坐标    │   Y 坐标   │
 * │ (4 字节)   │ (1 字节)  │  (4 字节)  │  (8 字节)   │  (8 字节)  │
 * └───────────┴───────────┴───────────┴────────────┴────────────┘
 * </pre>
 * <p>
 * 具体字段说明：<br>
 * - <b>SRID（4 字节）</b>: 空间参考系统 ID，始终以小端序存储（0xE6100000 对应 SRID 4326）。<br>
 * - <b>字节序标识（1 字节）</b>: 指示几何数据的字节序（0 = 大端，1 = 小端）。<br>
 * - <b>几何类型（4 字节）</b>: 指定 WKB 几何类型（1 = Point）。<br>
 * - <b>X 坐标（8 字节）</b>: 点的经度（Longitude）。<br>
 * - <b>Y 坐标（8 字节）</b>: 点的纬度（Latitude）。<br>
 */
public class WKBUtils {
    private WKBUtils() {
    }

    /**
     * 将 Point 对象转换为 WKB（Well-Known Binary）格式的字节数组。
     *
     * @param point 要转换的点对象，包含 SRID、经度、纬度信息。
     * @return 以 WKB 格式存储的字节数组。
     */
    public static byte[] writeWkbBytesFromPoint(Point point) {
        // 根据系统字节序选择 WKB 存储格式（除 SRID 外）
        ByteOrder order = point.getByteOrder() == null ? ByteOrder.LITTLE_ENDIAN : point.getByteOrder();
        // 分配 25 字节缓冲区，包含 SRID(4) + 字节序标识(1) + 几何类型(4) + 经度(8) + 纬度(8)
        ByteBuffer buffer = ByteBuffer.allocate(25);
        // 1. SRID（始终以小端序存储）
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(point.getSrid());
        // 2. 字节序标识（0 = 大端，1 = 小端）
        buffer.order(order);
        buffer.put((byte) (order == ByteOrder.BIG_ENDIAN ? 0 : 1));
        // 3. 几何类型（1 = Point）
        buffer.putInt(1);
        // 4. 坐标数据（X 和 Y 坐标）
        buffer.putDouble(point.getLongitude());
        buffer.putDouble(point.getLatitude());
        return buffer.array();
    }

    /**
     * 从 WKB（Well-Known Binary）字节数组中读取坐标数据，返回一个 {@link Point} 对象。
     * 该方法根据字节数组的内容反序列化经度和纬度，并将它们封装为 {@link Point} 对象。
     *
     * @param wkbBytes 长度为 25 字节的 WKB 数据，包含经纬度信息。
     * @return 包含经度和纬度信息的 {@link Point} 对象。
     */
    public static Point readPointFromWkbBytes(byte[] wkbBytes) {
        // 读取 SRID（前 4 个字节，固定使用小端序）
        int srid = ByteBuffer.wrap(wkbBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        // 判断字节顺序
        boolean isBigEndian = readIsWkbBigEndianByteOrder(wkbBytes[4]);
        // 读取经度和纬度，分别位于字节数组的 9 和 17 位置
        double x = readDoubleFromBytes(wkbBytes, 9, isBigEndian);
        double y = readDoubleFromBytes(wkbBytes, 17, isBigEndian);
        // 创建 Point 对象并设置经纬度
        return new Point(x, y, srid, isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 判断字节是否表示大端字节序。
     *
     * @param b 字节值，表示字节序标识。
     * @return 如果字节值为 0，则为大端字节序；否则为小端字节序。
     */
    private static boolean readIsWkbBigEndianByteOrder(byte b) {
        final byte BIG_ENDIAN = 0;
//        final byte LITTLE_ENDIAN = 1;
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
    private static double readDoubleFromBytes(byte[] buf, int offset, boolean bigEndian) {
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
    private static double readDoubleFromBytes(byte[] buf, boolean bigEndian) {
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
    private static long readLongFromBytesBigEndian(byte[] buf) {
        return (long) (buf[0] & 0xff) << 56
                | (long) (buf[1] & 0xff) << 48
                | (long) (buf[2] & 0xff) << 40
                | (long) (buf[3] & 0xff) << 32
                | (long) (buf[4] & 0xff) << 24
                | (long) (buf[5] & 0xff) << 16
                | (long) (buf[6] & 0xff) << 8
                | (buf[7] & 0xff);
    }

    /**
     * 从字节数组（小端字节序）中读取一个长整型数值。
     *
     * @param buf 字节数组，必须包含 8 个字节。
     * @return 反序列化后的长整型数值。
     */
    private static long readLongFromBytesLittleEndian(byte[] buf) {
        return (long) (buf[7] & 0xff) << 56
                | (long) (buf[6] & 0xff) << 48
                | (long) (buf[5] & 0xff) << 40
                | (long) (buf[4] & 0xff) << 32
                | (long) (buf[3] & 0xff) << 24
                | (long) (buf[2] & 0xff) << 16
                | (long) (buf[1] & 0xff) << 8
                | (buf[0] & 0xff);
    }

}
