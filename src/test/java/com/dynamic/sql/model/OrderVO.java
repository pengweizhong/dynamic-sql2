package com.dynamic.sql.model;

import com.dynamic.sql.entites.Order;
import com.dynamic.sql.entites.User;
import lombok.Data;

@Data
public class OrderVO {
    private Order order;
    private User user;
}

