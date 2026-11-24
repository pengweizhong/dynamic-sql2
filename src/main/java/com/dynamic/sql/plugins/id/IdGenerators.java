package com.dynamic.sql.plugins.id;

import com.dynamic.sql.enums.GenerationType;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** 按名称注册线程安全生成器；仅启动阶段注册，禁止覆盖运行实例以免重置雪花序列。 */
public final class IdGenerators {
    private static final ConcurrentMap<String,IdGenerator> GENERATORS=new ConcurrentHashMap<>();
    private IdGenerators() { }
    public static void register(String name,IdGenerator generator) {
        if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("Generator name is required");
        Objects.requireNonNull(generator,"Generator must not be null");
        if(GENERATORS.putIfAbsent(name,generator)!=null) throw new IllegalStateException("ID generator already registered: "+name);
    }
    public static IdGenerator get(String name) {
        // 原子地延迟创建默认实例，与 register 的 putIfAbsent 竞争时只保留一个实例。
        // 默认节点为 0；集群部署应在首次使用前注册各自的节点。
        IdGenerator generator=GenerationType.SNOWFLAKE.getDefaultGeneratorName().equals(name)
                ? GENERATORS.computeIfAbsent(name, key -> new SnowflakeIdGenerator(0))
                : GENERATORS.get(name);
        if(generator==null) throw new IllegalStateException("ID generator is not registered: "+name);
        return generator;
    }
}
