package com.dynamic.sql.core.column.function.scalar.conversion;

/** CAST 目标类型白名单，类型声明是 SQL 语法，不能用普通值占位符或任意字符串。 */
public final class CastTarget {
    public static final CastTarget SIGNED=new CastTarget("SIGNED");
    public static final CastTarget UNSIGNED=new CastTarget("UNSIGNED");
    public static final CastTarget DATE=new CastTarget("DATE");
    public static final CastTarget TIME=new CastTarget("TIME");
    public static final CastTarget DATETIME=new CastTarget("DATETIME");
    public static final CastTarget CHAR=new CastTarget("CHAR");
    public static final CastTarget BINARY=new CastTarget("BINARY");
    public static final CastTarget JSON=new CastTarget("JSON");
    private final String sql;
    private CastTarget(String sql) { this.sql=sql; }
    /** MySQL DECIMAL 精度最大 65，小数位最大 30。 */
    public static CastTarget decimal(int precision,int scale) {
        if(precision<1 || precision>65 || scale<0 || scale>30 || scale>precision)
            throw new IllegalArgumentException("Invalid DECIMAL precision or scale");
        return new CastTarget("DECIMAL("+precision+","+scale+")");
    }
    public static CastTarget character(int length) {
        if(length<1) throw new IllegalArgumentException("CHAR length must be positive");
        return new CastTarget("CHAR("+length+")");
    }
    public static CastTarget binary(int length) {
        if(length<1) throw new IllegalArgumentException("BINARY length must be positive");
        return new CastTarget("BINARY("+length+")");
    }
    String sql() { return sql; }
}
