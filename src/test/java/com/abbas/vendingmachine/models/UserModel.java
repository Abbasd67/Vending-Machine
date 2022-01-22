package com.abbas.vendingmachine.models;

import com.abbas.vendingmachine.entities.Enums;

public class UserModel {
    private String username;
    private String password;
    private Enums.Role role;

    public UserModel(String username, String password, Enums.Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Enums.Role getRole() {
        return role;
    }

    public void setRole(Enums.Role role) {
        this.role = role;
    }
}
