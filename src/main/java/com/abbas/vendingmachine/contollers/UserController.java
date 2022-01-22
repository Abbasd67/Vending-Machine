package com.abbas.vendingmachine.contollers;

import com.abbas.vendingmachine.configurations.JwtTokenUtil;
import com.abbas.vendingmachine.entities.Enums;
import com.abbas.vendingmachine.entities.Product;
import com.abbas.vendingmachine.entities.User;
import com.abbas.vendingmachine.models.*;
import com.abbas.vendingmachine.services.ProductService;
import com.abbas.vendingmachine.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        UserDetails userDetails = userService.login(model.getUsername(), model.getPassword());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/deposit")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> deposit(@RequestParam(value = "amount") int amount) throws ValidationException {
        Optional<Enums.DepositType> depositType = Enums.DepositType.valueOf(amount);
        if (depositType.isEmpty()) {
            throw new ValidationException("amount is not acceptable!!!");
        }
        User user = userService.getCurrentUser();
        int newAmount = userService.deposit(user.getId(), amount);
        return ResponseEntity.ok(newAmount);
    }

    @GetMapping("/reset")
    @PreAuthorize("hasAuthority('BUYER')")
    public HttpStatus reset() throws ValidationException {
        User user = userService.getCurrentUser();
        userService.reset(user.getId());
        return HttpStatus.OK;
    }

    @PostMapping("/buy")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> buy(@RequestBody BuyModel model) throws ValidationException {
        User user = userService.getCurrentUser();
        List<Integer> productIds = model
                .getProducts()
                .stream()
                .map(BuyProductModel::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productService.findAllById(productIds);
        int totalCost = productService.getTotalCost(model, products);
        if (totalCost > user.getDeposit()) {
            throw new ValidationException("Deposit is not enough!!!");
        }


        for (Product product : products) {
            Optional<BuyProductModel> productModel = model.getProducts().stream()
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
        List<Enums.DepositType> depositTypes = Arrays.stream(Enums.DepositType.values())
                .sorted((a, b) -> b.amount - a.amount)
                .collect(Collectors.toList());
        for (Enums.DepositType depositType : depositTypes) {
            while (remaining >= depositType.amount) {
                changes.add(depositType.amount);
                remaining -= depositType.amount;
            }
        }

        userService.reset(user.getId());

        BuyPostBackModel postBack = new BuyPostBackModel(products, totalCost, changes);
        return ResponseEntity.ok(postBack);
    }


}
