package com.dynamic.sql.entites;


import com.dynamic.sql.anno.Table;
import com.dynamic.sql.entites.enums.OrderStatus;
import com.dynamic.sql.entites.enums.PaymentMethod;
import com.dynamic.sql.entites.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {
    private int orderId;  // 订单 ID
    private int userId;  // 外键，关联 Users 表
    private Date orderDate;  // 订单日期
    private double totalAmount;  // 订单总金额
    private PaymentMethod paymentMethod;  // 支付方式
    private PaymentStatus paymentStatus;  // 支付状态
    private String shippingAddress;  // JSON 格式的发货地址
    private OrderStatus status;  // 订单状态
    private String orderDetails;  // JSON 格式的订单详情
}