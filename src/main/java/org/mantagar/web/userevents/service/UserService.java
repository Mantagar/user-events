package org.mantagar.web.userevents.service;

import org.mantagar.web.userevents.dto.UserDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exceptions.UserAlreadyExistsException;
import org.mantagar.web.userevents.exceptions.UserDoesNotExistException;
import org.mantagar.web.userevents.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Iterable<Integer> getAllUserIds() {
        return repository.findAllIds();
    }

    public User getUserById(Integer id) throws UserDoesNotExistException {
        return repository.findById(id).orElseThrow(UserDoesNotExistException::new);
    }

    public User createUser(UserDTO userDTO) throws UserAlreadyExistsException {
        // only unique pairs of name + surname are valid
        if (repository.existsByNameAndSurname(userDTO.name(), userDTO.surname())) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setName(userDTO.name());
        user.setSurname(userDTO.surname());
        return repository.save(user);
    }
}
