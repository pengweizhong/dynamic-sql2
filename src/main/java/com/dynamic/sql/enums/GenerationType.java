/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
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

    /**
     * 应用侧雪花 ID，默认使用共享生成器，也可在首次使用前自定义注册。
     */
    SNOWFLAKE("snowflake"),

    /**
     * 应用侧自定义主键算法，由注解名称选择扩展生成器。
     */
    CUSTOM;

    private final String defaultGeneratorName;

    GenerationType() {
        this.defaultGeneratorName = null;
    }

    GenerationType(String defaultGeneratorName) {
        this.defaultGeneratorName = defaultGeneratorName;
    }

    /**
     * @return 默认应用生成器名称；无默认生成器的策略返回 null
     */
    public String getDefaultGeneratorName() {
        return defaultGeneratorName;
    }
}
