/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.debugs;

import com.dynamic.sql.enums.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDebugger {
    private static final Logger log = LoggerFactory.getLogger(SqlDebugger.class);

    private SqlDebugger() {
    }

    public static void printSql(LogLevel level, String text, Object... args) {
        switch (level) {
            case TRACE:
                log.trace(text, args);
                break;
            case DEBUG:
                log.debug(text, args);
                break;
            case INFO:
                log.info(text, args);
                break;
            case WARN:
                log.warn(text, args);
                break;
            case ERROR:
                log.error(text, args);
                break;
            default:
                log.debug(text, args);
        }
    }
}
