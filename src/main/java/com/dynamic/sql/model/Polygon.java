package com.dynamic.sql.model;

import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.WKBUtils;

import java.io.Serializable;
import java.util.*;

public class Polygon implements Iterable<Point>, AttributeConverter<Polygon, byte[]>, Serializable {
    private static final long serialVersionUID = 345387899390164050L;

    private final List<Point> points;

    //提供反射
    private Polygon() {
        points = null;
    }

    public Polygon(Point x, Point y, Point z, Point... others) {
        this(true, x, y, z, others);
    }

    public Polygon(boolean autoClose, Point x, Point y, Point z, Point... others) {
        if (x == null || y == null || z == null) {
            throw new IllegalArgumentException("First three points (x, y, z) must not be null");
        }
        int n = others == null ? 0 : others.length;
        // 使用ArrayList初始化，可以直接计算需要的大小
        List<Point> pointsList = new ArrayList<>(3 + n);
        pointsList.add(x);
        pointsList.add(y);
        pointsList.add(z);
        if (others != null) {
            Collections.addAll(pointsList, others);
        }
        // 检查闭合
        checkClosure(pointsList, autoClose);
        // 不可变
        this.points = Collections.unmodifiableList(pointsList);
    }

    public Polygon(Collection<? extends Point> points) {
        this(true, points);
    }

    public Polygon(boolean autoClose, Collection<? extends Point> points) {
        if (CollectionUtils.isEmpty(points) || points.size() < 3) {
            throw new IllegalArgumentException("The Points collection provides at least three points");
        }
        List<Point> pointsClone = new ArrayList<>(points.size());
        for (Point point : points) {
            if (point == null) {
                throw new IllegalArgumentException("Point must not be null");
            }
            pointsClone.add(point);
        }
        // 检查闭合
        checkClosure(pointsClone, autoClose);
        this.points = Collections.unmodifiableList(pointsClone);
    }

    private void checkClosure(List<Point> points, boolean autoClose) {
        // 处理闭合
        Point firstPoint = points.get(0);
        Point lastPoint = points.get(points.size() - 1);
        if (!lastPoint.equals(firstPoint)) {
            if (autoClose) {
                // 自动闭合
                points.add(firstPoint);
            } else {
                throw new IllegalArgumentException("The closing point must be the same as the starting point");
            }
        }
    }

    public List<Point> getPoints() {
        return this.points;
    }

    @Override
    public Iterator<Point> iterator() {
        return points.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("POLYGON((");
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            sb.append(p.getLongitude()).append(" ").append(p.getLatitude());
            if (i < points.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("))");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polygon polygon = (Polygon) o;
        return points.equals(polygon.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }

    @Override
    public byte[] convertToDatabaseColumn(Polygon attribute) {
        return WKBUtils.writeWkbBytesFromPolygon(attribute);
    }

    @Override
    public Polygon convertToEntityAttribute(byte[] dbData) {
        return WKBUtils.readPolygonFromWkbBytes(dbData);
    }
}
