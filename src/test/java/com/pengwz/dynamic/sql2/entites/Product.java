package com.pengwz.dynamic.sql2.entites;

import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.GeneratedValue;
import com.pengwz.dynamic.sql2.anno.Id;
import com.pengwz.dynamic.sql2.anno.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {
    @Id
    @GeneratedValue
    private int productId;  // 产品 ID
    private String productName;  // 产品名称
    private double price;  // 产品价格
    private int stock;  // 产品库存
    private int categoryId;  // 外键，关联 Categories 表
    private String attributes;  // JSON 格式的产品属性
//    @Column(pattern = "yyyy-MM-dd")
    private Date createdAt;  // 产品创建日期
    private boolean isAvailable;  // 是否上架
}