package org.mantagar.web.userevents.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.publisher.PublisherTopic;
import org.mantagar.web.userevents.publisher.UserEventsPublisher;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.user.exception.UserNotFoundException;
import org.mantagar.web.userevents.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserEventsPublisher eventsPublisher;

    public Iterable<Long> getAllUserIds() {
        return userRepository.findAllIds();
    }

    public User getUser(Long id) {
        final Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOG.error("User with id={} not found", id);
            throw new UserNotFoundException("User with id=%d not found".formatted(id));
        }
        eventsPublisher.publishEvent(PublisherTopic.USER_BROWSED, user.get());
        return user.get();
    }

    @Transactional
    public User createUser(CreateUserRequest createUserRequest) {
        final User user = new User();
        user.setName(createUserRequest.name());
        user.setSurname(createUserRequest.surname());
        User createdUser = null;
        try {
            createdUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            handleDuplicatedUser(createUserRequest);
        }
        eventsPublisher.publishEvent(PublisherTopic.USER_CREATED, createdUser);
        return createdUser;
    }

    private void handleDuplicatedUser(CreateUserRequest createUserRequest) {
        String message = "User %s already exists".formatted(createUserRequest);
        LOG.error(message);
        throw new UserAlreadyExistsException(message);
    }
}
