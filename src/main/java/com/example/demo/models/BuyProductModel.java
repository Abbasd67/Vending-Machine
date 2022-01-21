package com.example.demo.models;

public class BuyProductModel {
    private int productId;
    private int amount;

    public BuyProductModel() {
    }

    public BuyProductModel(int productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
