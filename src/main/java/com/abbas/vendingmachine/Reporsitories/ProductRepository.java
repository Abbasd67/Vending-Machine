package com.abbas.vendingmachine.Reporsitories;

import com.abbas.vendingmachine.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Integer> {
    @Query("Select p from Product p where p.productName=?1")
    Optional<Product> findByName(String name);
}
