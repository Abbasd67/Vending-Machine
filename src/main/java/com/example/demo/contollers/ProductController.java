package com.example.demo.contollers;

import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.services.ProductService;
import com.example.demo.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Validated
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/product")
//    @PutMapping("/product")
    @PreAuthorize("hasAuthority('SELLER')")
    public Product postProduct(@RequestBody Product product) throws ValidationException {
        return setProduct(product);
    }

    @PutMapping("/product")
    @PreAuthorize("hasAuthority('SELLER')")
    public Product putProduct(@RequestBody Product product) throws ValidationException {
        return setProduct(product);
    }

    private Product setProduct(Product product) throws ValidationException {
        User user = userService.getCurrentUser();
        Optional<Product> productOptional = productService.findById(product.getId());
        if (productOptional.isEmpty()) {
            if (product.getId() > 0) {
                throw new ValidationException("Product not found!!!");
            }
            product.setSeller(user);
            return productService.save(product);
        } else {
            Product currentProduct = productOptional.get();
            if (!currentProduct.getSeller().getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new ValidationException("this product is not yours");
            }
            currentProduct.setCost(product.getCost());
            currentProduct.setAmountAvailable(product.getAmountAvailable());
            currentProduct.setProductName(product.getProductName());
            return productService.save(currentProduct);
        }
    }

    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public HttpStatus deleteProduct(@PathVariable("productId") int productId) throws ValidationException {
        User user = userService.getCurrentUser();
        Optional<Product> productOptional = productService.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ValidationException("Product not found!!!");
        }
        Product currentProduct = productOptional.get();
        if (!currentProduct.getSeller().getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new ValidationException("this product is not yours");
        }
        productService.delete(currentProduct);
        return HttpStatus.OK;
    }

}
