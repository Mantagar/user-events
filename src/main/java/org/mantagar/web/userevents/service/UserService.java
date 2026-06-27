package org.mantagar.web.userevents.service;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.dto.UserDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.publisher.PublisherTopic;
import org.mantagar.web.userevents.publisher.UserEventsPublisher;
import org.mantagar.web.userevents.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final UserEventsPublisher eventsPublisher;

    public Iterable<Integer> getAllUserIds() {
        return repository.findAllIds();
    }

    public User getUserById(Integer id) {
        final User user = repository.findById(id).orElseThrow(UserDoesNotExistException::new);
        eventsPublisher.publishEvent(PublisherTopic.USER_BROWSED, user);
        return user;
    }

    public User createUser(UserDTO userDTO) {
        // only unique pairs of name + surname are valid
        if (repository.existsByNameAndSurname(userDTO.name(), userDTO.surname())) {
            LOG.error("User {} already exists", userDTO);
            throw new UserAlreadyExistsException();
        }
        final User user = new User();
        user.setName(userDTO.name());
        user.setSurname(userDTO.surname());
        final User createdUser = repository.save(user);
        eventsPublisher.publishEvent(PublisherTopic.USER_CREATED, createdUser);
        return createdUser;
    }
}
