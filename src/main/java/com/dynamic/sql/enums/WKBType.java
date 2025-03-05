package com.dynamic.sql.enums;

public enum WKBType {

    POINT(1, "点"),
    LINESTRING(2, "线串"),
    POLYGON(3, "多边形"),
    MULTIPOINT(4, "多点"),
    MULTILINESTRING(5, "多线串"),
    MULTIPOLYGON(6, "多多边形"),
    GEOMETRYCOLLECTION(7, "几何集合"),
    ;

    private final int type;
    private final String description;

    WKBType(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
