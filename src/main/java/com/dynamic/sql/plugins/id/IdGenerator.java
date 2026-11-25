/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.id;

/**
 * 应用侧主键生成扩展点；共享实例必须线程安全，返回值不得为 NULL。
 */
@FunctionalInterface
public interface IdGenerator {
    Object generate(IdGenerationContext context);
}
