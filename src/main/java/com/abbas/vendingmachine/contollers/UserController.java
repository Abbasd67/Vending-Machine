package com.abbas.vendingmachine.contollers;

import com.abbas.vendingmachine.configurations.JwtTokenUtil;
import com.abbas.vendingmachine.entities.Enums;
import com.abbas.vendingmachine.entities.User;
import com.abbas.vendingmachine.models.JwtResponse;
import com.abbas.vendingmachine.models.LoginModel;
import com.abbas.vendingmachine.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@RestController
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

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


}
