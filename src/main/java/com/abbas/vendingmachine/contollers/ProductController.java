package com.abbas.vendingmachine.contollers;

import com.abbas.vendingmachine.entities.Product;
import com.abbas.vendingmachine.entities.User;
import com.abbas.vendingmachine.services.ProductService;
import com.abbas.vendingmachine.services.UserService;
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

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/product")
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
        return productService.setProduct(product);
    }

    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public HttpStatus deleteProduct(@PathVariable("productId") int productId) throws ValidationException {
        productService.deleteProduct(productId);
        return HttpStatus.OK;
    }

}
