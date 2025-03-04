package com.dynamic.sql.utils;

import com.dynamic.sql.model.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        if (wkbBytes.length == 21) {
            //没有 SRID，wkbBytes[0] 是字节序
            // 判断字节顺序
            ByteOrder byteOrder = readIsBigOrLittle(wkbBytes[0]);
            // 读取经度和纬度
            //经度和纬度的顺序可能取决于字节顺序的设置或者 WKB 序列化时的约定。某些情况下 WKB 会将纬度（Y）放在经度（X）之前
            //在 WKB标准 中，通常 经度（Longitude） 存储为 X 坐标，纬度（Latitude） 存储为 Y 坐标。
            //这意味着，正常情况下，WKB数据中的第一个8字节应该代表经度（X），第二个8字节应该代表纬度（Y）。
            //然而，在某些数据库（如 MySQL 或其他 GIS 扩展）中，可能会出现经度和纬度位置交换的情况。这通常是由于数据库对空间数据类型的内部实现有关，或者特定的实现方式。
            //这可能与数据库的空间参考系统（SRID）相关，例如一些数据库可能对特定空间参考系统（如 WGS84）进行特定的编码。
            //但是截至目前为止，未能发现原因，似乎与执行函数相关
            double x = readDoubleFromBytes(wkbBytes, 5, byteOrder);
            double y = readDoubleFromBytes(wkbBytes, 13, byteOrder);
            return new Point(x, y, 0, byteOrder);
        }
        if (wkbBytes.length == 25) {
            // 读取 SRID（前 4 个字节，固定使用小端序）
            int srid = ByteBuffer.wrap(wkbBytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            // 判断字节顺序
            ByteOrder byteOrder = readIsBigOrLittle(wkbBytes[4]);
            // 读取经度和纬度，分别位于字节数组的 9 和 17 位置
            double x = readDoubleFromBytes(wkbBytes, 9, byteOrder);
            double y = readDoubleFromBytes(wkbBytes, 17, byteOrder);
            return new Point(x, y, srid, byteOrder);
        }
        throw new IllegalArgumentException("Invalid WKB point length: " + wkbBytes.length);
    }

    /**
     * 判断字节是否表示大端字节序。
     *
     * @param b 字节值，表示字节序标识。
     * @return 如果字节值为 0，则为大端字节序；否则为小端字节序。
     */
    private static ByteOrder readIsBigOrLittle(byte b) {
        final byte BIG_ENDIAN = 0;
        // 如果字节值为 BIG_ENDIAN（0），表示大端字节序
        return b == BIG_ENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    /**
     * 从字节数组中读取一个双精度浮点数（double），支持大端和小端字节序。
     *
     * @param buf       字节数组，必须至少包含 8 个字节。
     * @param offset    偏移量，表示从字节数组中的哪个位置开始读取。
     * @param byteOrder 字节序
     * @return 反序列化后的双精度浮点数（double）。
     */
    private static double readDoubleFromBytes(byte[] buf, int offset, ByteOrder byteOrder) {
        // 从字节数组中获取 8 字节数据并转换为 double 值
        ByteBuffer buffer = ByteBuffer.wrap(buf, offset, 8);
        buffer.order(byteOrder);
        return buffer.getDouble();
    }

}
