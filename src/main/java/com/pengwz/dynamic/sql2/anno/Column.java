package com.pengwz.dynamic.sql2.anno;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;

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

}
