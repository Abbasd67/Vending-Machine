package com.example.demo.configurations;

import com.example.demo.Reporsitories.ProductRepository;
import com.example.demo.Reporsitories.UserRepository;
import com.example.demo.entities.Enums;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class DataConfig {

    @Bean
    CommandLineRunner userBean(UserRepository repository){
        return args -> {
            User admin = new User("Admin","Admin", Enums.Role.ADMIN);
            User buyer = new User("Buyer","Buyer", Enums.Role.BUYER);
            User seller = new User("Seller","Seller", Enums.Role.SELLER);
            repository.saveAll(List.of(admin, buyer, seller));
        };
    }

    @Bean
    CommandLineRunner productBean(ProductRepository repository, UserRepository userRepository) {
        return args -> {
            Product product1 = new Product("product1", 100, 10);
            Product product2 = new Product("product2", 10, 15);
            Product product3 = new Product("product3", 20, 30);
            Optional<User> seller = userRepository.findByUsername("Seller");
            if(seller.isPresent()){
                product1.setSeller(seller.get());
                product2.setSeller(seller.get());
                product3.setSeller(seller.get());
            }
            repository.saveAll(List.of(product1, product2, product3));
        };
    }
}
