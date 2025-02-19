package com.dynamic.sql.plugins.conversion;


import java.util.Map;

public interface FetchResultConverter<R> extends ObjectValueConverter<Map<String, Object>, R> {
    @Override
    R convertValueTo(Map<String, Object> value);

    /**
     * 获取转换器的目标类型。
     * <p>
     * 该方法提供了目标类型的显式声明。子类可以通过实现此方法来返回泛型类型 {@code R} 的具体类型，
     * 以便在需要时（例如反射或类型检查）能够明确获取目标类型。
     * </p>
     * <p>
     * 默认实现返回 {@code null}，子类可以重写此方法提供具体的类型。
     * </p>
     *
     * @return 目标类型的 Class 对象，或者 {@code null}（如果未提供）。
     */
    default Class<R> getTargetType() {
        return null;
    }
}
