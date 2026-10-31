package com.dynamic.sql.testing.unit;

import com.dynamic.sql.model.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 集合几何对象的结构、类型和损坏载荷验证，不依赖数据库。 */
class GeometryObjectTest {
    /** 集合成员必须保留类型和坐标，读取返回的 Point 不能修改内部状态。 */
    @Test void roundTripsStructuredMembers() {
        MultiPoint points=new MultiPoint(3857,new Point(1,2,3857),new Point(3,4,3857));
        GeometryCollection collection=new GeometryCollection(3857,points,new Geometry(new LineString(
                new Point(0,0,3857),new Point(2,3,3857))));
        GeometryCollection read=collection.convertToEntityAttribute(collection.convertToDatabaseColumn(collection));
        assertEquals(collection,read);
        assertEquals(2,read.getGeometries().size());
        read.getGeometries().get(0).getGeometries().get(0).getPoints().get(0).setLongitude(99);
        assertEquals(1,read.getGeometries().get(0).getGeometries().get(0).getPoints().get(0).getLongitude());
        assertNull(collection.convertToEntityAttribute(null));
    }

    /** 固定大端 WKB 点样本验证 SRID 头、X/Y 和载荷字节序独立处理。 */
    @Test void readsIndependentBigEndianFixture() {
        byte[] bytes=ByteBuffer.allocate(25).order(ByteOrder.LITTLE_ENDIAN).putInt(3857)
                .put((byte)0).order(ByteOrder.BIG_ENDIAN).putInt(1).putDouble(12).putDouble(34).array();
        Geometry point=Geometry.read(bytes);
        assertEquals(3857,point.getSrid());
        assertEquals(12,point.getPoints().get(0).getLongitude());
        assertEquals(34,point.getPoints().get(0).getLatitude());
    }

    /** 错误类型、超大成员数、截断载荷、尾随垃圾与混合 SRID 均须拒绝。 */
    @Test void rejectsCorruptCollections() {
        MultiPoint points=new MultiPoint(0,new Point(1,2));
        byte[] bytes=points.convertToDatabaseColumn(points);
        byte[] count=bytes.clone();
        ByteBuffer.wrap(count).order(ByteOrder.LITTLE_ENDIAN).putInt(9,Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class,()->Geometry.read(count));
        assertThrows(IllegalArgumentException.class,()->Geometry.read(Arrays.copyOf(bytes,bytes.length-1)));
        assertThrows(IllegalArgumentException.class,()->Geometry.read(Arrays.copyOf(bytes,bytes.length+1)));
        assertThrows(IllegalArgumentException.class,()->new MultiLineString(0).convertToEntityAttribute(bytes));
        assertThrows(IllegalArgumentException.class,()->new MultiPoint(0,new Point(1,2,3857)));
    }
}
