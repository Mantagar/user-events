package org.mantagar.web.userevents.shared;

import org.mantagar.web.userevents.user.exception.UserDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<String> handleUserDoesNotExistException(UserDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
    }
}
