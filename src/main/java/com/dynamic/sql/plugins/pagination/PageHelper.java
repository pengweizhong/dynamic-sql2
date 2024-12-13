package com.dynamic.sql.plugins.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static com.dynamic.sql.plugins.pagination.LocalPage.clearCurrentPage;
import static com.dynamic.sql.plugins.pagination.LocalPage.setCurrentPage;


/**
 * PageHelper 用于处理不同类型的数据分页逻辑的辅助类。
 * <p>
 * {@link this#of(int, int)}：通用分页逻辑<br>
 * {@link this#ofCollection(int, int)}：集合类型分页逻辑<br>
 * {@link this#ofMap(int, int)}：Map类型分页逻辑
 */
public class PageHelper {
    private static final Logger log = LoggerFactory.getLogger(PageHelper.class);

    private PageHelper() {
    }

    /**
     * 创建一个通用的 GeneralPageHelper 实例，指定页码和页大小。
     *
     * @param pageIndex 要获取的页码（如果小于等于0，则页码默认为1）
     * @param pageSize  每页的大小（每页的项目数量）
     * @return GeneralPageHelper 实例，用于通用的分页处理
     * @throws IllegalArgumentException 如果 pageSize 小于等于0
     */
    public static GeneralPageHelper of(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new GeneralPageHelper(Math.max(pageIndex, 1), pageSize);
    }

    /**
     * 创建一个 CollectionPageHelper 实例，指定页码和页大小。
     * 针对处理集合类型的分页。
     *
     * @param pageIndex 要获取的页码（如果小于等于0，则页码默认为1）
     * @param pageSize  每页的大小（每页的项目数量）
     * @return CollectionPageHelper 实例，用于集合的分页处理
     * @throws IllegalArgumentException 如果 pageSize 小于等于0
     */
    public static CollectionPageHelper ofCollection(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new CollectionPageHelper(Math.max(pageIndex, 1), pageSize);
    }

    /**
     * 创建一个 MapPageHelper 实例，指定页码和页大小。
     * 针对处理 Map 类型的分页。
     *
     * @param pageIndex 要获取的页码（如果小于等于0，则页码默认为1）
     * @param pageSize  每页的大小（每页的项目数量）
     * @return MapPageHelper 实例，用于 Map 的分页处理
     * @throws IllegalArgumentException 如果 pageSize 小于等于0
     */
    public static MapPageHelper ofMap(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new MapPageHelper(Math.max(pageIndex, 1), pageSize);
    }

    static <T> GeneralPageHelper of(PageInfo<T> pageInfo) {
        return new GeneralPageHelper(pageInfo);
    }


    static <C extends Collection<T>, T> CollectionPageHelper ofCollection(CollectionPage<C, T> collectionPage) {
        return new CollectionPageHelper(collectionPage);
    }


    static <K, V, M extends Map<K, V>> MapPageHelper ofMap(MapPage<K, V, M> mapPage) {
        return new MapPageHelper(mapPage);
    }

    private static void checkPageParams(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            log.warn("Invalid pageIndex: {}, setting to 1", pageIndex);
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be >= 1");
        }
    }

    @SuppressWarnings("unchecked")
    public static class CollectionPageHelper {
        private int pageIndex;
        private int pageSize;
        private CollectionPage<?, ?> cacheCollectionPage;

        private CollectionPageHelper(int pageIndex, int pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        private <C extends Collection<T>, T> CollectionPageHelper(CollectionPage<C, T> collectionPage) {
            this.cacheCollectionPage = collectionPage;
        }

        public <C extends Collection<T>, T> CollectionPage<C, T> selectPage(Supplier<C> selectSupplier) {
            CollectionPage<C, T> collectionPage;
            if (cacheCollectionPage == null) {
                collectionPage = new CollectionPage<>(pageIndex, pageSize);
            } else {
                collectionPage = (CollectionPage<C, T>) cacheCollectionPage;
            }
            executeQuery(collectionPage, selectSupplier);
            return collectionPage;
        }
    }

    @SuppressWarnings("unchecked")
    public static class MapPageHelper {
        private int pageIndex;
        private int pageSize;
        private MapPage<?, ?, ?> cacheMapPage;

        private MapPageHelper(int pageIndex, int pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        private <K, V, M extends Map<K, V>> MapPageHelper(MapPage<K, V, M> mapPage) {
            this.cacheMapPage = mapPage;
        }

        public <K, V, M extends Map<K, V>> MapPage<K, V, M> selectPage(Supplier<M> selectSupplier) {
            MapPage<K, V, M> mapPage;
            if (cacheMapPage == null) {
                mapPage = new MapPage<>(pageIndex, pageSize);
            } else {
                mapPage = (MapPage<K, V, M>) cacheMapPage;
            }
            executeQuery(mapPage, selectSupplier);
            return mapPage;
        }
    }

    @SuppressWarnings("unchecked")
    public static class GeneralPageHelper {
        private int pageIndex;
        private int pageSize;
        private PageInfo<?> cachePageInfo;

        private GeneralPageHelper(int pageIndex, int pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        private <T> GeneralPageHelper(PageInfo<T> pageInfo) {
            this.cachePageInfo = pageInfo;
        }

        public <T> PageInfo<T> selectPage(Supplier<T> selectSupplier) {
            PageInfo<T> pageInfo;
            if (cachePageInfo == null) {
                pageInfo = new PageInfo<>(pageIndex, pageSize);
            } else {
                pageInfo = (PageInfo<T>) cachePageInfo;
            }
            executeQuery(pageInfo, selectSupplier);
            return pageInfo;
        }
    }

    static void executeQuery(AbstractPage abstractPage, Supplier<?> selectSupplier) {
        setCurrentPage(abstractPage);
        try {
            abstractPage.setRecords(selectSupplier);
        } finally {
            clearCurrentPage();
        }
    }
}
