package com.example.demo.configurations;

import com.example.demo.Reporsitories.ProductRepository;
import com.example.demo.Reporsitories.UserRepository;
import com.example.demo.entities.Enums;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

@Configuration
public class DataConfig {

    @Bean
    CommandLineRunner userBean(UserRepository repository){
        return args -> {
            var admin = new User("Admin","Admin", Enums.Role.ADMIN);
            var buyer = new User("Buyer","Buyer", Enums.Role.BUYER);
            var seller = new User("Seller","Seller", Enums.Role.SELLER);
            repository.saveAll(List.of(admin, buyer, seller));
        };
    }

    @Bean
    CommandLineRunner productBean(ProductRepository repository, UserRepository userRepository) {
        return args -> {
            var product1 = new Product("product1", 100, 10);
            var product2 = new Product("product2", 10, 15);
            var product3 = new Product("product3", 20, 30);
            var seller = userRepository.findByUsername("Seller");
            if(seller.isPresent()){
                product1.setSeller(seller.get());
                product2.setSeller(seller.get());
                product3.setSeller(seller.get());
            }
            repository.saveAll(List.of(product1, product2, product3));
        };
    }
}
