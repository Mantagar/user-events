package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mantagar.web.userevents.publisher.UserEventsPublisher;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.user.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.user.model.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository mockUserRepository;
    @Mock private UserEventsPublisher mockEventsPublisher;
    @InjectMocks private UserService userService;

    @Test
    void shouldReturnIdList() {
        List<Long> expected = List.of(1L, 2L, 3L);
        when(mockUserRepository.findAllBy()).thenReturn(expected);

        assertEquals("", expected, userService.getAllUserIds());
    }

    @Test
    void shouldReturnUser_whenUserExists() {
        User expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");
        when(mockUserRepository.findById(5L)).thenReturn(Optional.of(expectedUser));

        User createdUser = assertDoesNotThrow(() -> userService.getUser(5L));
        assertEquals("", expectedUser, createdUser);
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        assertThrows(UserDoesNotExistException.class, () -> userService.getUser(5L));
    }

    @Test
    void shouldReturnUser_whenUserCreated() {
        CreateUserRequest testCreateUserRequest = new CreateUserRequest("test", "test");
        User expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");
        when(mockUserRepository.existsByNameAndSurname("test", "test")).thenReturn(false);
        when(mockUserRepository.save(any(User.class))).thenReturn(expectedUser);

        User createdUser = assertDoesNotThrow(() -> userService.createUser(testCreateUserRequest));
        assertEquals("", expectedUser, createdUser);
    }

    @Test
    void shouldThrowException_whenUserAlreadyExists() {
        CreateUserRequest testCreateUserRequest = new CreateUserRequest("test", "test");
        User expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");
        when(mockUserRepository.existsByNameAndSurname("test", "test")).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(testCreateUserRequest));
    }
}
