package com.pengwz.dynamic.sql2.plugins.pagination;

class LocalPage {
    private LocalPage() {
    }

    private static final ThreadLocal<AbstractPage> LOCAL_CURRENT_PAGE = new ThreadLocal<>();//NOSONAR

    public static AbstractPage getCurrentPage() {
        AbstractPage abstractPage = LOCAL_CURRENT_PAGE.get();
//        if (abstractPage == null) {
//            throw new IllegalArgumentException("Missing required paging parameters");
//        }
        return abstractPage;
    }

    public static void setCurrentPage(AbstractPage page) {
        LOCAL_CURRENT_PAGE.set(page);
    }

    public static void remove() {
        LOCAL_CURRENT_PAGE.remove();
    }
}
