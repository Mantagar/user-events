package org.mantagar.web.userevents.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mantagar.web.userevents.dto.UserNameSurnameDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.publisher.UserEventsPublisher;
import org.mantagar.web.userevents.repository.UserRepository;
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
        List<Long> expected = List.of(1, 2, 3);
        when(mockUserRepository.findAllBy()).thenReturn(expected);

        assertEquals("", expected, userService.getAllUserIds());
    }

    @Test
    void shouldReturnUser_whenUserExists() {
        User expectedUser = new User();
        expectedUser.setId(5);
        expectedUser.setName("test");
        expectedUser.setSurname("test");
        when(mockUserRepository.findById(5)).thenReturn(Optional.of(expectedUser));

        User createdUser = assertDoesNotThrow(() -> userService.getUser(5));
        assertEquals("", expectedUser, createdUser);
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        assertThrows(UserDoesNotExistException.class, () -> userService.getUser(5));
    }

    @Test
    void shouldReturnUser_whenUserCreated() {
        UserNameSurnameDTO testUserNameSurnameDTO = new UserNameSurnameDTO("test", "test");
        User expectedUser = new User();
        expectedUser.setId(5);
        expectedUser.setName("test");
        expectedUser.setSurname("test");
        when(mockUserRepository.existsByNameAndSurname("test", "test")).thenReturn(false);
        when(mockUserRepository.save(any(User.class))).thenReturn(expectedUser);

        User createdUser = assertDoesNotThrow(() -> userService.createUser(testUserNameSurnameDTO));
        assertEquals("", expectedUser, createdUser);
    }

    @Test
    void shouldThrowException_whenUserAlreadyExists() {
        UserNameSurnameDTO testUserNameSurnameDTO = new UserNameSurnameDTO("test", "test");
        User expectedUser = new User();
        expectedUser.setId(5);
        expectedUser.setName("test");
        expectedUser.setSurname("test");
        when(mockUserRepository.existsByNameAndSurname("test", "test")).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(testUserNameSurnameDTO));
    }
}
