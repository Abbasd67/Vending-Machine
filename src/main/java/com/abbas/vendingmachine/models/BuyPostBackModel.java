package com.abbas.vendingmachine.models;

import com.abbas.vendingmachine.entities.Product;

import java.util.List;

public class BuyPostBackModel {
    private List<Product> products;
    private int totalSpent;
    private List<Integer> changes;

    public BuyPostBackModel(List<Product> products, int totalSpent, List<Integer> changes) {
        this.products = products;
        this.totalSpent = totalSpent;
        this.changes = changes;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(int totalSpent) {
        this.totalSpent = totalSpent;
    }

    public List<Integer> getChanges() {
        return changes;
    }

    public void setChanges(List<Integer> changes) {
        this.changes = changes;
    }
}
