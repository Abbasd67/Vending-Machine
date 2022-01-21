package com.example.demo.models;

import java.util.List;

public class BuyModel {

    private List<BuyProductModel> products;

    public BuyModel() {
    }

    public BuyModel(List<BuyProductModel> products) {
        this.products = products;
    }

    public List<BuyProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<BuyProductModel> products) {
        this.products = products;
    }

}

