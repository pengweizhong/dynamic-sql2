/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.exception;

import java.util.Map;

public interface SqlErrorHint {
    /**
     * SQL错误提示基类，用于定义数据库异常码与人类可读提示语之间的映射关系。
     *
     * <p>继承该抽象类可实现不同数据库的错误码提示集合，以支持多数据库系统的异常识别与用户友好提示。</p>
     *
     * <p>示例用途：</p>
     * <pre>{@code
     * public class MySqlErrorHint extends SqlErrorHint {
     *     public Map<String, String> getErrorHints() {
     *         return Map.of("23000", "插入失败：主键或唯一约束冲突");
     *     }
     * }
     * }</pre>
     */
    Map<String, String> getErrorHints();
}
