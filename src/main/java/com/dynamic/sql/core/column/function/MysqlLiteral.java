package com.dynamic.sql.core.column.function;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** 只能使用字面量的 MySQL 语法位置：标识符白名单与安全文本编码。 */
public final class MysqlLiteral {
    private MysqlLiteral() { }
    public static String hex(String value) {
        StringBuilder sql=new StringBuilder("X'");
        for(byte b:value.getBytes(StandardCharsets.UTF_8)) sql.append(String.format(Locale.ROOT,"%02x",b&255));
        return sql.append("'").toString();
    }
    /** JSON_TABLE 路径要求文本字面量；拒绝模式相关反斜线及控制字符，避免跨 SQL 模式含义变化。 */
    public static String text(String value) {
        if(value==null || value.indexOf(92)>=0 || value.indexOf(0)>=0)
            throw new IllegalArgumentException("Unsupported SQL literal content");
        return "'" + value.replace("'","''") + "'";
    }
    public static String identifier(String value) {
        if(value==null || !value.matches("[A-Za-z_][A-Za-z0-9_]*"))
            throw new IllegalArgumentException("Invalid SQL identifier");
        return "`"+value+"`";
    }
    /** 默认值只接收 JSON 标量，避免引入序列化依赖或隐式接受任意业务对象。 */
    public static String jsonScalar(Object value) {
        if(value==null) return "null";
        if(value instanceof Boolean) return value.toString();
        if(value instanceof Number) {
            String number=value.toString();
            if(!number.matches("-?(0|[1-9][0-9]*)([.][0-9]+)?([eE][+-]?[0-9]+)?"))
                throw new IllegalArgumentException("Invalid JSON numeric default");
            return number;
        }
        if(!(value instanceof CharSequence) && !(value instanceof Character))
            throw new IllegalArgumentException("JSON default must be scalar");
        StringBuilder json=new StringBuilder();json.append((char)34);
        for(char c:value.toString().toCharArray()) {
            if(c==34 || c==92) { json.append((char)92).append(c); }
            else if(c<32) {
                json.append((char)92).append('u').append(String.format(Locale.ROOT,"%04x",(int)c));
            } else json.append(c);
        }
        return json.append((char)34).toString();
    }

}
