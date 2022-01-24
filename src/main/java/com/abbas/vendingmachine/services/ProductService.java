package com.abbas.vendingmachine.services;

import com.abbas.vendingmachine.Reporsitories.ProductRepository;
import com.abbas.vendingmachine.entities.Product;
import com.abbas.vendingmachine.entities.User;
import com.abbas.vendingmachine.models.BuyModel;
import com.abbas.vendingmachine.models.BuyProductModel;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Product setProduct(Product product) throws ValidationException {
        User user = getCurrentUser();
        Optional<Product> productOptional = findById(product.getId());
        if (productOptional.isEmpty()) {
            if (product.getId() > 0) {
                throw new ValidationException("Product not found!!!");
            }
            product.setSeller(user);
            return save(product);
        } else {
            Product currentProduct = productOptional.get();
            if (!currentProduct.getSeller().getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new ValidationException("this product is not yours");
            }
            currentProduct.setCost(product.getCost());
            currentProduct.setAmountAvailable(product.getAmountAvailable());
            currentProduct.setProductName(product.getProductName());
            return save(currentProduct);
        }
    }

    public void deleteProduct(int productId) throws ValidationException{
        User user = getCurrentUser();
        Optional<Product> productOptional = findById(productId);
        if (productOptional.isEmpty()) {
            throw new ValidationException("Product not found!!!");
        }
        Product currentProduct = productOptional.get();
        if (!currentProduct.getSeller().getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new ValidationException("this product is not yours");
        }
        delete(currentProduct);
    }

    public int getTotalCost(BuyModel model, List<Product> products) throws ValidationException {
        int totalCost = 0;
        for (Product product : products) {
            Optional<BuyProductModel> productModel = model.getProducts().stream()
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
            Optional<Product> oldProduct = findByName(product.getProductName());
            if (oldProduct.isPresent()) {
                throw new ValidationException("Product with same name exist!!!");
            }
        }
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }


    public User getCurrentUser() throws ValidationException {
        return userService.getCurrentUser();
    }
}
