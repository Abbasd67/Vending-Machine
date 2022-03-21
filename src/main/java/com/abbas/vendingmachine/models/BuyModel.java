package com.abbas.vendingmachine.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuyModel {

    private List<BuyProductModel> products;

    public BuyModel() {
    }

    public BuyModel(List<BuyProductModel> products) {
        this.products = products;
    }

}

