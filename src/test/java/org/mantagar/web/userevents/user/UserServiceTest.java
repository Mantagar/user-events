package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.event.UserEventPublisher;
import org.mantagar.web.userevents.user.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.user.exception.UserNotFoundException;
import org.mantagar.web.userevents.user.model.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository mockUserRepository;
    @Mock private UserEventPublisher mockEventsPublisher;
    @InjectMocks private UserService userService;

    @Test
    void getAllUserIds_always_returnsIds() {
        var expected = List.of(1L, 2L, 3L);

        when(mockUserRepository.findAllIds()).thenReturn(expected);

        assertEquals(expected, userService.getAllUserIds());
    }

    @Test
    void getUser_userExists_returnsUser() {
        var expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(mockUserRepository.findById(5L)).thenReturn(Optional.of(expectedUser));

        var createdUser = assertDoesNotThrow(() -> userService.getUser(5L));
        assertEquals(expectedUser, createdUser);
    }

    @Test
    void getUser_userDoesNotExist_throwsUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUser(5L));
    }

    @Test
    void createUser_userDoesNotExists_returnsUser() {
        var createUserRequest = new CreateUserRequest("test", "test");
        var expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(mockUserRepository.save(any(User.class))).thenReturn(expectedUser);

        assertEquals(expectedUser, userService.createUser(createUserRequest));
    }

    @Test
    void createUser_userAlreadyExists_throwsUserAlreadyExistsException() {
        var createUserRequest = new CreateUserRequest("test", "test");
        var expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(mockUserRepository.existsByNameAndSurname("test", "test")).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class, () -> userService.createUser(createUserRequest));
    }
}
