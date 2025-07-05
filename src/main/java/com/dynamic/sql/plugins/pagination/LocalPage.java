/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.pagination;


class LocalPage {
    private static final ThreadLocal<AbstractPage> LOCAL_CURRENT_PAGE = new ThreadLocal<>();//NOSONAR

    private LocalPage() {
    }

    public static AbstractPage getCurrentPage() {
        return LOCAL_CURRENT_PAGE.get();
    }

    public static void setCurrentPage(AbstractPage page) {
        LOCAL_CURRENT_PAGE.set(page);
    }

    public static void clearCurrentPage() {
        LOCAL_CURRENT_PAGE.remove();
    }
}
