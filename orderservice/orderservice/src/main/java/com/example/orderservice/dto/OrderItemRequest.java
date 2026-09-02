
package com.example.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class OrderItemRequest {

    @NotBlank(message = "productName is mandatory")
    private String productName;

    @Min(value = 1, message = "quantity must be greater than 0")
    private int quantity;

    public OrderItemRequest() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
