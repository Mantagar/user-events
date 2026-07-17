package org.mantagar.web.userevents.controller;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.dto.UserNameSurnameDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Iterable<Long> getAllUserIds() {
        return userService.getAllUserIds();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserNameSurnameDTO userNameSurnameDTO) {
        return userService.createUser(userNameSurnameDTO);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
    }
}
