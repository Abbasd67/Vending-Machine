package com.abbas.vendingmachine.Reporsitories;

import com.abbas.vendingmachine.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("Select u from User u where u.username=?1")
    Optional<User> findByUsername(String username);

}