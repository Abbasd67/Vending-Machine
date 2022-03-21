package com.abbas.vendingmachine.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModel {
    private String username;
    private String password;
    private String role;

    public UserModel() {
    }

    public UserModel(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
