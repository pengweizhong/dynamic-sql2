package com.dynamic.sql.entites;

import lombok.Data;

@Data
public class ProductView {
    private int productId;
    private String productName;

    @Override
    public String toString() {
        String sss = "";
        if (productName != null) {
            sss = ", productName='" + productName + '\'';
        }
        return "ProductView{" +
                "productId=" + productId + sss +
                "}";
    }
}
