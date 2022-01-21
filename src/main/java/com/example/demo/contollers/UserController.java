package com.example.demo.contollers;

import com.example.demo.configurations.JwtTokenUtil;
import com.example.demo.entities.Enums;
import com.example.demo.entities.User;
import com.example.demo.models.*;
import com.example.demo.services.ProductService;
import com.example.demo.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/user")
    public HttpStatus register(@RequestBody User user) throws ValidationException {
        userService.addUser(user);
        return HttpStatus.OK;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel model) {
        var userDetails = userService.login(model.getUsername(), model.getPassword());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/deposit")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> deposit(@RequestParam(value = "amount") int amount) throws ValidationException {
        var depositType = Enums.DepositType.valueOf(amount);
        if (depositType.isEmpty()) {
            throw new ValidationException("amount is not acceptable!!!");
        }
        var user = userService.getCurrentUser();
        var newAmount = userService.deposit(user.getId(), amount);
        return ResponseEntity.ok(newAmount);
    }

    @GetMapping("/reset")
    @PreAuthorize("hasAuthority('BUYER')")
    public HttpStatus reset() throws ValidationException {
        var user = userService.getCurrentUser();
        userService.reset(user.getId());
        return HttpStatus.OK;
    }

    @PostMapping("/buy")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> buy(@RequestBody BuyModel model) throws ValidationException {
        var user = userService.getCurrentUser();
        var productIds = model
                .getProducts()
                .stream()
                .map(BuyProductModel::getProductId)
                .collect(Collectors.toList());

        var products = productService.findAllById(productIds);
        int totalCost = productService.getTotalCost(model, products);
        if (totalCost > user.getDeposit()) {
            throw new ValidationException("Deposit is not enough!!!");
        }


        for (var product : products) {
            var productModel = model.getProducts().stream()
                    .filter(p -> p.getProductId() == product.getId())
                    .findFirst();
            if (productModel.isEmpty()) {
                continue;
            }
            product.setAmountAvailable(product.getAmountAvailable() - productModel.get().getAmount());
            productService.save(product);
        }

        int remaining = user.getDeposit() - totalCost;
        ArrayList<Integer> changes = new ArrayList<>();
        var depositTypes = Arrays.stream(Enums.DepositType.values())
                .sorted((a, b) -> b.amount - a.amount)
                .collect(Collectors.toList());
        for (var depositType : depositTypes) {
            while (remaining >= depositType.amount) {
                changes.add(depositType.amount);
                remaining -= depositType.amount;
            }
        }

        userService.reset(user.getId());

        var postBack = new BuyPostBackModel(products, totalCost, changes);
        return ResponseEntity.ok(postBack);
    }


}
