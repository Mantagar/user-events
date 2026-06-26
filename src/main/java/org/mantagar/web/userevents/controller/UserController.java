package org.mantagar.web.userevents.controller;

import jakarta.validation.Valid;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public Iterable<Integer> getAllUserIds() {
        return service.getAllUserIds();
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable Integer userId) {
        return service.getUserById(userId);
    }

    @PostMapping("/")
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }
}
