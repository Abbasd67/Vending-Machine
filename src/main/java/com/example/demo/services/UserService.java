package com.example.demo.services;

import com.example.demo.Reporsitories.UserRepository;
import com.example.demo.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDetails login(String username, String password) throws UsernameNotFoundException {
        var user = findByUsername(username);
        if (user.isEmpty() || !user.get().getPassword().equalsIgnoreCase(password)) {
            throw new UsernameNotFoundException("User not found with this Username and Password!!!");
        }
        return loadUserByUsername(username);
    }

    public void addUser(User user) throws ValidationException {
        var oldUser = findByUsername(user.getUsername());
        if (oldUser.isPresent()) {
            throw new ValidationException("User exist with same username");
        }
        userRepository.save(user);
    }

    public int deposit(int userId, int amount) throws ValidationException {
        var user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found!!!"));
        user.setDeposit(user.getDeposit() + amount);
        return userRepository.save(user).getDeposit();
    }

    public void reset(int userId) throws ValidationException {
        var user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found!!!"));
        user.setDeposit(0);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new RuntimeException("User not found: " + username));
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), List.of(authority));
    }

    public User getCurrentUser() throws ValidationException {
        var user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        var currentUser = findByUsername(user.getUsername());
        if (currentUser.isEmpty()) {
            throw new ValidationException("User not found!!!");
        }
        return currentUser.get();
    }
}