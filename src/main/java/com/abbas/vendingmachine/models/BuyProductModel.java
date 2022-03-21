package com.abbas.vendingmachine.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyProductModel {
    private int productId;
    private int amount;

    public BuyProductModel() {
    }

    public BuyProductModel(int productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }

}
