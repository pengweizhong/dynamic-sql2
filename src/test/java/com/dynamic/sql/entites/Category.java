package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("categories")
public class Category {
    private Integer categoryId;  // 分类 ID
    private String categoryName;  // 分类名称
    private String description;  // 分类描述
}
