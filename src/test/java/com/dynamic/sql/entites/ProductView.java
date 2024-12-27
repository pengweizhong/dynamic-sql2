package com.dynamic.sql.entites;

import com.dynamic.sql.anno.View;
import lombok.Data;

@Data
@View
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
