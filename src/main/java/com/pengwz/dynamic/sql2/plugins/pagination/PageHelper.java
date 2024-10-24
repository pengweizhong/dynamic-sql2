package com.pengwz.dynamic.sql2.plugins.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static com.pengwz.dynamic.sql2.plugins.pagination.LocalPage.setCurrentPage;

public class PageHelper {
    private static final Logger log = LoggerFactory.getLogger(PageHelper.class);
    final AbstractPage page;

    private PageHelper(Supplier<AbstractPage> supplier) {
        page = supplier.get();
        setCurrentPage(page);
    }

    static PageHelper of(Supplier<AbstractPage> supplier) {
        return new PageHelper(supplier);
    }

    /**
     * 创建一个新的 {@code PageHelper} 实例，并设置分页的页码和每页大小。
     * <p>
     * 该方法会验证传入的 {@code pageIndex} 和 {@code pageSize} 是否有效。如果 {@code pageIndex} 小于 1，
     * 会自动将其设置为 1，并记录警告日志。如果 {@code pageSize} 小于 1，将抛出 {@code IllegalArgumentException} 异常，
     * 因为每页大小必须至少为 1。
     * </p>
     *
     * @param pageIndex 页码，必须 >= 1；如果小于 1，将重置为 1。
     * @param pageSize  每页的记录数，必须 >= 1；如果小于 1，将抛出 {@code IllegalArgumentException}。
     * @return 返回 {@code PageHelper} 实例，包含有效的分页信息。
     * @throws IllegalArgumentException 如果 {@code pageSize} 小于 1。
     */
    public static PageHelper of(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            log.warn("Invalid pageIndex: {}, setting to 1", pageIndex);
            pageIndex = 1;
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be >= 1");
        }
        final int pi = pageIndex;
        return new PageHelper(() -> new PageInfo<>(pi, pageSize));
    }

    public static PageHelper ofMap(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            log.warn("Invalid pageIndex: {}, setting to 1", pageIndex);
            pageIndex = 1;
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be >= 1");
        }
        final int pi = pageIndex;
        return new PageHelper(() -> new MapPage<>(pi, pageSize));
    }


    /**
     * 执行分页查询，并将查询结果设置到 {@code PageInfo} 对象中。
     *
     * @param <C>            查询结果集合的类型，必须是 {@code Collection<T>} 的子类。
     * @param <T>            查询结果中的元素类型。
     * @param selectSupplier 提供查询结果的 {@code Supplier}，其 {@code get()} 方法返回一个包含查询结果的集合。
     * @return 包含查询结果的 {@code PageInfo} 对象
     */
    @SuppressWarnings("unchecked")
    public <C extends Collection<T>, T> PageInfo<C, T> selectPageInfo(Supplier<C> selectSupplier) {
        page.setRecords(selectSupplier);
        return (PageInfo<C, T>) page;
    }

    @SuppressWarnings("unchecked")
    public <K, V, M extends Map<K, V>> MapPage<K, V, M> selectMapPageInfo(Supplier<M> selectSupplier) {
        page.setRecords(selectSupplier);
        return (MapPage<K, V, M>) page;
    }


}
