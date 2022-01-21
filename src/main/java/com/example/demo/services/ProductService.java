package com.example.demo.services;

import com.example.demo.Reporsitories.ProductRepository;
import com.example.demo.entities.Product;
import com.example.demo.models.BuyModel;
import com.example.demo.models.BuyProductModel;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Product save(Product product) throws ValidationException {
        if (product.getId() <= 0) {
            var oldProduct = findByName(product.getProductName());
            if (oldProduct.isPresent()) {
                throw new ValidationException("Product with same name exist!!!");
            }
        }
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public int getTotalCost(BuyModel model, List<Product> products) throws ValidationException {
        int totalCost = 0;
        for (var product : products) {
            var productModel = model.getProducts().stream()
                    .filter(p -> p.getProductId() == product.getId())
                    .findFirst();
            if (productModel.isEmpty()) {
                continue;
            }
            if (productModel.get().getAmount() > product.getAmountAvailable()) {
                throw new ValidationException("the maximum amount remaining for product with id: "
                        + product.getId() + " is " + product.getAmountAvailable());
            }
            totalCost += productModel.get().getAmount() * product.getCost();
        }

        return totalCost;
    }

    public List<Product> findAllById(Iterable<Integer> productIds) {
        return productRepository.findAllById(productIds);
    }
}
