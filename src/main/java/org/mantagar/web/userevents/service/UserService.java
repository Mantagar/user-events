package org.mantagar.web.userevents.service;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.dto.UserNameSurnameDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.publisher.PublisherTopic;
import org.mantagar.web.userevents.publisher.UserEventsPublisher;
import org.mantagar.web.userevents.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserEventsPublisher eventsPublisher;

    public Iterable<Long> getAllUserIds() {
        return userRepository.findAllBy();
    }

    public User getUser(Long id) {
        final Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOG.error("User with id={} not found", id);
            throw new UserDoesNotExistException("User with id=%d not found".formatted(id));
        }
        eventsPublisher.publishEvent(PublisherTopic.USER_BROWSED, user.get());
        return user.get();
    }

    public User createUser(UserNameSurnameDTO userNameSurnameDTO) {
        // only unique pairs of name + surname are valid - that could be achieved by unique constraint TODO
        if (userRepository.existsByNameAndSurname(
                userNameSurnameDTO.name(), userNameSurnameDTO.surname())) {
            LOG.error("User {} already exists", userNameSurnameDTO);
            throw new UserAlreadyExistsException("User %s already exists".formatted(userNameSurnameDTO));
        }
        final User user = new User();
        user.setName(userNameSurnameDTO.name());
        user.setSurname(userNameSurnameDTO.surname());
        final User createdUser = userRepository.save(user);
        eventsPublisher.publishEvent(PublisherTopic.USER_CREATED, createdUser);
        return createdUser;
    }
}
