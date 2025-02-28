package com.dynamic.sql.plugins.pagination;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.condition.WhereCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
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
        private PageInfo<?> pageInfo;

        private GeneralPageHelper(int pageIndex, int pageSize) {
            pageInfo = new PageInfo<>(pageIndex, pageSize);
        }

        private <T> GeneralPageHelper(PageInfo<T> pageInfo) {
            this.pageInfo = pageInfo;
        }

        /**
         * 执行分页查询并返回分页结果 {@link PageInfo} 对象。
         * <p>
         * 此方法作为分页操作的入口，使用提供的 {@code selectSupplier} 来执行分页查询并返回查询结果。
         * 它将查询结果封装在 {@code PageInfo<T>} 对象中，该对象包含查询数据和分页信息（如当前页、总页数等）。
         * </p>
         *
         * <p><strong>分页逻辑：</strong></p>
         * <ul>
         *     <li>使用 {@code selectSupplier} 执行查询</li>
         *     <li>根据提供的页码和页大小进行分页</li>
         *     <li>返回的 {@code PageInfo} 对象包含分页数据及分页相关信息</li>
         * </ul>
         *
         * <p><strong>示例：</strong></p>
         * <pre>{@code
         * PageInfo<List<ProductView>> pageInfo = PageHelper.of(1, 3).selectPage(this::selectProductViewList);
         * }</pre>
         * 在上述示例中，调用 {@code selectPage} 方法执行查询，并返回第一页的 3 条数据。
         *
         * @param selectSupplier 查询数据的 {@link Supplier}
         * @param <T>            查询结果的类型。
         * @return 返回包含查询结果和分页信息的 {@link PageInfo} 对象。
         * @see SqlContext#select()
         */
        public <T> PageInfo<T> selectPage(Supplier<T> selectSupplier) {
            executeQuery(pageInfo, selectSupplier);
            return (PageInfo<T>) pageInfo;
        }

        /**
         * 将查询条件应用到当前的分页查询中
         * <p>
         * 示例：
         * <pre>{@code
         *         PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
         *                 .applyWhere(whereCondition -> whereCondition.andGreaterThanOrEqualTo(bindName("userId"), 3))
         *                 .selectPage(() -> sqlContext.select().allColumn().from(User.class).fetch().toList());
         *         pageInfo.getRecords().forEach(System.out::println);
         * }</pre>
         * <p>注意：此方法不会支持链式调用，调用该方法时会将条件与现有分页信息合并。，为避免数据污染，多次调用仅最后一次生效。</p>
         *
         * @param condition 需要应用的查询条件，使用 {@link WhereCondition} 来构建具体的查询条件。该参数是一个 Consumer 类型，
         *                  用于接收条件并进行操作。如果传入的 condition 为 {@code null}，则不会进行任何条件应用操作。
         * @return 返回当前的 {@link GeneralPageHelper} 实例
         */
        public GeneralPageHelper applyWhere(Consumer<WhereCondition> condition) {
            if (condition != null) {
                pageInfo = new ConditionPageInfo<>(pageInfo, condition);
                return this;
            }
            //reset
            pageInfo = new PageInfo<>(pageInfo.getPageIndex(), pageInfo.getPageSize());
            return this;
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
