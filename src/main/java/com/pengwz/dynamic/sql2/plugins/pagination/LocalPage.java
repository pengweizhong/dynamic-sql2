package com.pengwz.dynamic.sql2.plugins.pagination;

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

    public static void remove() {
        LOCAL_CURRENT_PAGE.remove();
    }
}
