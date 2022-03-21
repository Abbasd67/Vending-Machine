package com.abbas.vendingmachine.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "benutzer")
public class User {

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private int id;
    private String username;
    private String password;
    private int deposit;
    private Enums.Role role;

    @OneToMany(mappedBy = "seller", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public User() {

    }

    public User(String username, String password, Enums.Role role) {
        this(username, password, role, 0);
    }

    public User(String username, String password, Enums.Role role, int deposit) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.deposit = deposit;
    }

}
