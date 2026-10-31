package com.dynamic.sql.testing.unit;

import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.model.Point;
import com.dynamic.sql.model.Polygon;
import com.dynamic.sql.utils.WKBUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/** 使用独立构造的二进制验证空间转换，不能只用框架自身的写入结果检验自身读取。 */
class PolygonWkbTest {
    private static final double[][] COORDINATES={{1,2},{4,2},{4,6},{1,2}};

    /** 同时验证大小端载荷与非零 SRID，防止零值和对称坐标掩盖字节序及 X/Y 错误。 */
    @Test void readsIndependentBinaryWithoutSwappingAxes() {
        for (ByteOrder order : new ByteOrder[]{ByteOrder.LITTLE_ENDIAN,ByteOrder.BIG_ENDIAN}) {
            Polygon polygon=WKBUtils.readPolygonFromWkbBytes(binary(order));
            for(int i=0;i<COORDINATES.length;i++) {
                Point point=polygon.getPoints().get(i);
                assertEquals(COORDINATES[i][0],point.getLongitude());
                assertEquals(COORDINATES[i][1],point.getLatitude());
                assertEquals(4326,point.getSrid()); assertEquals(order,point.getByteOrder());
            }
            assertArrayEquals(binary(order),WKBUtils.writeWkbBytesFromPolygon(polygon));
        }
    }

    /** 未指定字节序的业务对象也应可以写入，并明确使用小端格式。 */
    @Test void defaultByteOrderIsLittleEndian() {
        Polygon polygon=new Polygon(new Point(1,2,4326),new Point(4,2,4326),new Point(4,6,4326));
        assertArrayEquals(binary(ByteOrder.LITTLE_ENDIAN),WKBUtils.writeWkbBytesFromPolygon(polygon));
    }

    /** 对损坏输入直接报错，不自动补齐闭环，也不按异常点数分配大集合。 */
    @Test void rejectsMalformedPayload() {
        assertThrows(IllegalArgumentException.class,()->WKBUtils.readPolygonFromWkbBytes(new byte[16]));
        byte[] marker=binary(ByteOrder.LITTLE_ENDIAN); marker[4]=2;
        assertThrows(IllegalArgumentException.class,()->WKBUtils.readPolygonFromWkbBytes(marker));
        byte[] count=binary(ByteOrder.LITTLE_ENDIAN); ByteBuffer.wrap(count).order(ByteOrder.LITTLE_ENDIAN).putInt(13,Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class,()->WKBUtils.readPolygonFromWkbBytes(count));
        byte[] open=binary(ByteOrder.LITTLE_ENDIAN); ByteBuffer.wrap(open).order(ByteOrder.LITTLE_ENDIAN).putDouble(17+3*16,99);
        assertThrows(DynamicSqlException.class,()->WKBUtils.readPolygonFromWkbBytes(open));
    }

    //SRID 固定小端；标准 WKB 载荷包含字节序、类型、单环点数和坐标。
    private static byte[] binary(ByteOrder order) {
        ByteBuffer buffer=ByteBuffer.allocate(17+COORDINATES.length*16).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(4326); buffer.order(order); buffer.put((byte)(order==ByteOrder.LITTLE_ENDIAN?1:0));
        buffer.putInt(3); buffer.putInt(1); buffer.putInt(COORDINATES.length);
        for(double[] point:COORDINATES) { buffer.putDouble(point[0]); buffer.putDouble(point[1]); }
        return buffer.array();
    }
}
