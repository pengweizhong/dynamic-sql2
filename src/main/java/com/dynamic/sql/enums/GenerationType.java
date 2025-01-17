package com.dynamic.sql.enums;


import com.dynamic.sql.anno.GeneratedValue;

public enum GenerationType {

    /**
     * 使用数据库自增策略进行自增；
     * 比如mysql的自增策略，oracle触发器自增策略。
     */
    AUTO,

    /**
     * 使用序列进行自增，当使用序列类型自增时，需要执行序列名。目前已验证的仅Oracle支持
     *
     * @see GeneratedValue#sequenceName()
     */
    SEQUENCE,
}
