package com.abbas.vendingmachine.models;

import com.abbas.vendingmachine.entities.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuyPostBackModel {
    private List<Product> products;
    private int totalSpent;
    private List<Integer> changes;

    public BuyPostBackModel(List<Product> products, int totalSpent, List<Integer> changes) {
        this.products = products;
        this.totalSpent = totalSpent;
        this.changes = changes;
    }

}
