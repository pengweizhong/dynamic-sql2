package com.pengwz.dynamic.sql2.entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAndOrderView {
    //                .column(User::getUserId)
    private Long userId;
    //                .column(User::getName, "user_name")
    private String userName;
    //                .column("user_total", "total_spent")
    private BigDecimal totalSpent;
    //                .column("user_total", "total_orders")
    private BigDecimal totalOrders;
    //                .column("p", Product::getProductName)
    private String productName;
    //                .column("p", Product::getPrice)
    private BigDecimal price;
    //                .column(Category::getCategoryName)
    private String categoryName;
    //                .column("p", Product::getStock)
    private int stock;
}
