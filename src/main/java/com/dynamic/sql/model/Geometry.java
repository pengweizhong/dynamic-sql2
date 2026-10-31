package com.dynamic.sql.model;

import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.utils.WKBUtils;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** 通用几何值对象，保存结构化坐标、环和子几何，可承载七种基本二维几何类型。 */
public class Geometry implements AttributeConverter<Geometry,byte[]> {
    protected int type;
    protected int srid;
    protected List<double[]> coordinates = new ArrayList<>();
    protected List<List<double[]>> rings = new ArrayList<>();
    protected List<Geometry> geometries = new ArrayList<>();

    /** 供框架反射创建转换器。 */
    protected Geometry() { }

    /** 从已有标准模型创建通用几何，保持坐标和 SRID。 */
    public Geometry(Point point) { copy(read(WKBUtils.writeWkbBytesFromPoint(point))); }
    public Geometry(LineString line) { copy(read(line.convertToDatabaseColumn(line))); }
    public Geometry(Polygon polygon) { copy(read(WKBUtils.writeWkbBytesFromPolygon(polygon))); }

    /** 内部共享不可变解析结果，公开访问器均返回副本或不可变列表。 */
    protected final void copy(Geometry geometry) {
        type=geometry.type; srid=geometry.srid; coordinates=geometry.coordinates;
        rings=geometry.rings; geometries=geometry.geometries;
    }

    /** 构建多几何或集合时检查成员类型与坐标系一致性。 */
    protected Geometry(int type, int srid, Geometry... members) {
        this.type=type; this.srid=srid;
        for (Geometry member : members) {
            if (member == null || member.srid != srid)
                throw new IllegalArgumentException("Geometry members must be non-null and use the same SRID");
            if (type != 7 && member.type != type-3)
                throw new IllegalArgumentException("Unexpected geometry member type");
            Geometry snapshot = read(member.convertToDatabaseColumn(member));
            geometries.add(snapshot);
        }
    }

    public int getSrid() { return srid; }
    public int getGeometryType() { return type; }

    /** 点和线的坐标以独立 Point 对象返回，不暴露内部数组。 */
    public List<Point> getPoints() { return points(coordinates); }
    private List<Point> points(List<double[]> source) {
        List<Point> result=new ArrayList<>();
        for (double[] xy : source) result.add(new Point(xy[0],xy[1],srid));
        return Collections.unmodifiableList(result);
    }
    /** 多边形按外环、内环顺序返回，支持带孔多边形。 */
    public List<List<Point>> getRings() {
        List<List<Point>> result=new ArrayList<>();
        for (List<double[]> ring : rings) result.add(points(ring));
        return Collections.unmodifiableList(result);
    }
    public List<Geometry> getGeometries() { return Collections.unmodifiableList(geometries); }

    @Override public byte[] convertToDatabaseColumn(Geometry value) {
        if (value == null) return null;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        writeInt(out,value.srid); encode(out,value);
        return out.toByteArray();
    }
    @Override public Geometry convertToEntityAttribute(byte[] bytes) { return bytes == null ? null : read(bytes); }

    /** 读取 MySQL 固定小端 SRID 头及递归 WKB；限制嵌套深度，防止损坏载荷耗尽栈。 */
    public static Geometry read(byte[] bytes) {
        if (bytes == null || bytes.length < 9) throw new IllegalArgumentException("Invalid MySQL geometry payload");
        ByteBuffer input=ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int srid=input.getInt();
        Geometry result=decode(input,srid,0);
        if (input.hasRemaining()) throw new IllegalArgumentException("Trailing geometry bytes");
        return result;
    }
    private static Geometry decode(ByteBuffer in,int srid,int depth) {
        if (depth>64 || in.remaining()<5) throw new IllegalArgumentException("Invalid geometry depth or header");
        int marker=Byte.toUnsignedInt(in.get());
        if (marker>1) throw new IllegalArgumentException("Invalid geometry byte order");
        in.order(marker==1?ByteOrder.LITTLE_ENDIAN:ByteOrder.BIG_ENDIAN);
        Geometry g=new Geometry(); g.type=in.getInt(); g.srid=srid;
        switch (g.type) {
            case 1: g.coordinates=readPoints(in,1); break;
            case 2: g.coordinates=readPoints(in,count(in,16)); break;
            case 3:
                int rings=count(in,4);
                for (int i=0;i<rings;i++) {
                    List<double[]> ring=readPoints(in,count(in,16));
                    if (ring.size()<4 || !Arrays.equals(ring.get(0),ring.get(ring.size()-1)))
                        throw new IllegalArgumentException("Polygon ring must be closed");
                    g.rings.add(ring);
                }
                break;
            case 4: case 5: case 6: case 7:
                int members=count(in,5);
                for (int i=0;i<members;i++) {
                    Geometry member=decode(in,srid,depth+1);
                    if (g.type!=7 && member.type!=g.type-3) throw new IllegalArgumentException("Invalid geometry member type");
                    g.geometries.add(member);
                }
                break;
            default: throw new IllegalArgumentException("Unsupported geometry type");
        }
        return g;
    }
    private static int count(ByteBuffer in,int minimum) {
        if (in.remaining()<4) throw new IllegalArgumentException("Missing geometry count");
        int value=in.getInt();
        if (value<0 || value>in.remaining()/minimum) throw new IllegalArgumentException("Invalid geometry count");
        return value;
    }
    private static List<double[]> readPoints(ByteBuffer in,int count) {
        if (count>in.remaining()/16) throw new IllegalArgumentException("Truncated coordinates");
        List<double[]> result=new ArrayList<>();
        for (int i=0;i<count;i++) {
            double x=in.getDouble(),y=in.getDouble();
            if (!Double.isFinite(x)||!Double.isFinite(y)) throw new IllegalArgumentException("Coordinates must be finite");
            result.add(new double[]{x,y});
        }
        return result;
    }
    /** 写入统一小端编码，子几何不重复携带 SRID 头。 */
    private static void encode(ByteArrayOutputStream out,Geometry g) {
        out.write(1); writeInt(out,g.type);
        if (g.type==1) writePoints(out,g.coordinates);
        else if (g.type==2) { writeInt(out,g.coordinates.size()); writePoints(out,g.coordinates); }
        else if (g.type==3) {
            writeInt(out,g.rings.size());
            for (List<double[]> ring:g.rings) { writeInt(out,ring.size()); writePoints(out,ring); }
        } else {
            writeInt(out,g.geometries.size());
            for (Geometry member:g.geometries) encode(out,member);
        }
    }
    private static void writeInt(ByteArrayOutputStream out,int value) {
        byte[] bytes=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        out.write(bytes,0,bytes.length);
    }
    private static void writePoints(ByteArrayOutputStream out,List<double[]> points) {
        for (double[] xy:points) {
            byte[] bytes=ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN).putDouble(xy[0]).putDouble(xy[1]).array();
            out.write(bytes,0,bytes.length);
        }
    }
    @Override public boolean equals(Object value) {
        return value instanceof Geometry && Arrays.equals(convertToDatabaseColumn(this),convertToDatabaseColumn((Geometry)value));
    }
    @Override public int hashCode() { return Arrays.hashCode(convertToDatabaseColumn(this)); }
}
