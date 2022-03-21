package com.abbas.vendingmachine.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table
public class Product {

    @Id
    @SequenceGenerator(name = "product_sequence", sequenceName = "product_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_sequence")
    private int id;
    private String productName;
    private int amountAvailable;
    private int cost;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @JsonIgnore
    private User seller;


    public Product(String productName, int amountAvailable, int cost) {
        this.productName = productName;
        this.amountAvailable = amountAvailable;
        this.cost = cost;
    }

    public Product() {

    }

}
