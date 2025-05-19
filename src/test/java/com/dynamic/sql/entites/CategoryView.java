package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.View;
import lombok.Data;

import java.util.List;

@Data
@View
public class CategoryView {
    private Integer categoryId;  // 分类 ID
    private String categoryName;  // 分类名称
    private String description;  // 分类描述
    @Column("productVOS")
    private List<ProductVO> productVOS;

    @Data
    public static class ProductVO {
        private Integer categoryId;  // 外键，关联 Categories 表
        private Integer productId;  // 产品 ID
        private String productName;  // 产品名称
    }
}
