/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.exception;

public class DynamicSqlException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    //SQLException.getSQLState()
//    private String sqlState;

    public DynamicSqlException(String message) {
        super(message);
    }

    public DynamicSqlException(Exception e) {
        super(e);
    }
}
