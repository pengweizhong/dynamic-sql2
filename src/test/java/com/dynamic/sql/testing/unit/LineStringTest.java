package com.dynamic.sql.testing.unit;

import com.dynamic.sql.model.LineString;
import com.dynamic.sql.model.Point;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/** 线串对象与独立二进制样本核对，防止编码解码的相同错误相互抵消。 */
class LineStringTest {
    /** 大小端载荷与固定小端 SRID 头分别验证，点的 X/Y 不能交换。 */
    @Test void readsAndWritesIndependentEndianFixtures() {
        for (ByteOrder order : new ByteOrder[]{ByteOrder.LITTLE_ENDIAN,ByteOrder.BIG_ENDIAN}) {
            ByteBuffer buffer = ByteBuffer.allocate(45).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(3857).put((byte)(order == ByteOrder.LITTLE_ENDIAN ? 1 : 0)).order(order);
            buffer.putInt(2).putInt(2).putDouble(1).putDouble(2).putDouble(3).putDouble(4);
            LineString line = new LineString(new Point(1,2,3857,order),new Point(3,4,3857,order));
            assertArrayEquals(buffer.array(),line.convertToDatabaseColumn(line));
            assertEquals(line,line.convertToEntityAttribute(buffer.array()));
            assertEquals(3857,line.convertToEntityAttribute(buffer.array()).getSrid());
        }
    }

    /** 损坏标记、错误类型、超大点数和不完整载荷都必须明确拒绝。 */
    @Test void rejectsDamagedPayloads() {
        LineString line = new LineString(new Point(1,2),new Point(3,4));
        byte[] data = line.convertToDatabaseColumn(line);
        assertThrows(IllegalArgumentException.class,()->line.convertToEntityAttribute(Arrays.copyOf(data,44)));
        byte[] marker = data.clone(); marker[4]=2;
        assertThrows(IllegalArgumentException.class,()->line.convertToEntityAttribute(marker));
        byte[] type = data.clone(); type[5]=1;
        assertThrows(IllegalArgumentException.class,()->line.convertToEntityAttribute(type));
        byte[] count = data.clone(); ByteBuffer.wrap(count).order(ByteOrder.LITTLE_ENDIAN).putInt(9,Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class,()->line.convertToEntityAttribute(count));
    }

    /** 外部点及读取副本的变更不能修改已构造的线串。 */
    @Test void copiesMutablePointsAndPreservesNull() {
        Point point = new Point(1,2);
        LineString line = new LineString(point,new Point(3,4));
        point.setLongitude(99);
        line.getPoints().get(0).setLongitude(88);
        assertEquals(1,line.getPoints().get(0).getLongitude());
        assertNull(line.convertToDatabaseColumn(null));
        assertNull(line.convertToEntityAttribute(null));
    }

    /** 模型拒绝单点、混合坐标系和非有限坐标。 */
    @Test void validatesModelInputs() {
        assertThrows(IllegalArgumentException.class,()->new LineString(Arrays.asList(new Point(1,2))));
        assertThrows(IllegalArgumentException.class,()->new LineString(new Point(1,2,0),new Point(3,4,3857)));
        assertThrows(IllegalArgumentException.class,()->new LineString(new Point(Double.NaN,2),new Point(3,4)));
    }
}
