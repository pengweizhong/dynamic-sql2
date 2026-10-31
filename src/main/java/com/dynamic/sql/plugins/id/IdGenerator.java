package com.dynamic.sql.plugins.id;

/** 应用侧主键生成扩展点；共享实例必须线程安全，返回值不得为 NULL。 */
@FunctionalInterface
public interface IdGenerator {
    Object generate(IdGenerationContext context);
}
