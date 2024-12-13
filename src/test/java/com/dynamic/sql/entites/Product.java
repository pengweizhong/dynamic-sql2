package com.dynamic.sql.entites;


import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Integer productId;  // 产品 ID
    private String productName;  // 产品名称
    private BigDecimal price;  // 产品价格
    private Integer stock;  // 产品库存
    private Integer categoryId;  // 外键，关联 Categories 表
    private String attributes;  // JSON 格式的产品属性
    //    @Column(pattern = "yyyy-MM-dd")
    private Date createdAt;  // 产品创建日期
    private Boolean isAvailable;  // 是否上架
}