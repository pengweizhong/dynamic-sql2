/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */

package com.dynamic.sql.anno;


import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.plugins.conversion.DefaultAttributeConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {
    /**
     * 给当前字段设置映射数据库的列名，如果没有指定，则根据驼峰规则自动转换为蛇形规则。
     *
     * @return 数据库列名
     */
    String value() default "";

    /**
     * 忽略字段，被忽略的字段将不参与数据库交互
     *
     * @return 是否忽略当前字段
     */
    boolean ignore() default false;

    /**
     * 当发生字段发生冲突时，告诉程序应该选择哪个字段。通常这种情况发生在父子类中。
     *
     * @return 将当前字段设置为唯一项
     */
    boolean primary() default false;

    /**
     * 定义数据库值和实体类值的转换器，该转换器必须允许空的构造函数
     *
     * @return 转换器
     */
    Class<? extends AttributeConverter> converter() default DefaultAttributeConverter.class;

    /**
     * 格式化日期和数字类型的字段。
     * <p>
     * 该注解可以应用于日期（Date 类型）和数字（如 Double、Integer 等类型）字段，
     * 用于指定字段值在存储到数据库之前的格式化方式。
     * <p>
     * 对于日期字段，格式应遵循 `SimpleDateFormat` 支持的日期模式（例如：yyyy-MM-dd）。
     * 对于数字字段，格式化方式可以参考 `DecimalFormat` 的模式（例如：#,###.##）。
     * <p>
     * 在框架处理中，字段值会根据该格式化方式进行格式化，并插入到数据库中。
     *
     * @return 格式化的模式或模式字符串
     */
    String format() default "";

    /**
     * 指定空间参考系统的 SRID（Spatial Reference System Identifier）。
     * <p>
     * SRID（空间参考系统标识符）用于定义地理空间数据的坐标系统。
     * 在数据库中，SRID 决定了点、线、面等几何数据的地理投影方式。
     * <p>
     * 例如：
     * <ul>
     *   <li>WGS 84（GPS 常用坐标系）的 SRID 为 4326。</li>
     *   <li>Web 墨卡托投影（用于 Web 地图）的 SRID 为 3857。</li>
     * </ul>
     * <p>
     * 当 SRID 设置为 <b>0</b>（默认值）时，表示未指定 SRID，数据库使用默认的坐标系。
     * <p>
     * <b>注意：此方法目前暂未使用，未来版本可能会启用。</b>
     *
     * @return 空间参考系统的 SRID
     */
    int srid() default 0;
}
