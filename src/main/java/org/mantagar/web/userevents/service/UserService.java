package org.mantagar.web.userevents.service;

import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exceptions.UserAlreadyExistsException;
import org.mantagar.web.userevents.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Iterable<Integer> getAllUserIds() {
        return repository.findAllIds();
    }

    public Optional<User> getUserById(Integer id) {
        return repository.findById(id);
    }

    public User createUser(User user) {
        // TODO for now if both name and surname are the same then abort
        if (repository.existsByNameAndSurname(user.getName(), user.getSurname())) {
            throw new UserAlreadyExistsException();
        }
        return repository.save(user);
    }
}
