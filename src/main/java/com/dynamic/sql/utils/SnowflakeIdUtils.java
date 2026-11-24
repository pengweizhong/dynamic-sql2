/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.utils;

import com.dynamic.sql.enums.GenerationType;

import com.dynamic.sql.plugins.id.IdGenerator;
import com.dynamic.sql.plugins.id.IdGenerators;
import com.dynamic.sql.plugins.id.SnowflakeIdGenerator;

/**
 * 业务雪花 ID 入口，与 SNOWFLAKE 主键策略共用名为 snowflake 的生成器。
 * <p>可直接调用（默认节点编号为 0），或在首次使用前调用 {@code SnowflakeIdUtils.init(1)}。业务中调用
 * {@code SnowflakeIdUtils.nextId()}。若已注册 snowflake 生成器，可直接调用。
 * 默认配置适用于单实例。集群内每个进程必须分配不同的节点编号（0～1023），且禁止重复初始化。
 * 同节点编号不可另建生成器并行使用；重启时应确保时钟已超过上次发号时间。
 * 时钟回拨或序列耗尽时沿用生成器的异常行为。
 */
public final class SnowflakeIdUtils {
    private static final String GENERATOR_NAME = GenerationType.SNOWFLAKE.getDefaultGeneratorName();

    private SnowflakeIdUtils() {
    }

    /**
     * 在应用启动阶段注册共享生成器，只需调用一次。
     *
     * @param nodeId 部署方分配的唯一节点编号，范围 0～1023
     * @throws IllegalArgumentException 节点编号超出范围
     * @throws IllegalStateException snowflake 生成器已注册
     */
    public static void init(long nodeId) {
        IdGenerators.register(GENERATOR_NAME, new SnowflakeIdGenerator(nodeId));
    }

    /**
     * 线程安全地生成雪花 ID。
     *
     * @return 非负的雪花 ID
     * @throws IllegalStateException 生成器类型不符或时钟异常
     */
    public static long nextId() {
        IdGenerator generator = IdGenerators.get(GENERATOR_NAME);
        if (!(generator instanceof SnowflakeIdGenerator)) {
            throw new IllegalStateException("snowflake must be registered as a SnowflakeIdGenerator");
        }
        return ((SnowflakeIdGenerator) generator).nextId();
    }

    /** @return 雪花 ID 的十进制字符串，适用于字符串主键及前端传输 */
    public static String nextIdStr() {
        return Long.toString(nextId());
    }
}
