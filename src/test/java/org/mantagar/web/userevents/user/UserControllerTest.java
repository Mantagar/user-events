package org.mantagar.web.userevents.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.user.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;

    @Test
    void getAllUserIds_always_returns200() throws Exception {
        List<Long> expected = List.of(1L, 2L, 3L);
        when(userService.getAllUserIds()).thenReturn(expected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expected.size()));
    }

    @Test
    void getUser_userExists_returns200() throws Exception {
        User expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(userService.getUser(5L)).thenReturn(expectedUser);

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.surname").value("test"));
    }

    @Test
    void getUser_userNotFound_returns404() throws Exception {
        when(userService.getUser(5L)).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(get("/users/5")).andExpect(status().isNotFound());
    }

    @Test
    void createUser_userDoesNotExist_returns201() throws Exception {
        CreateUserRequest testCreateUserRequest = new CreateUserRequest("test", "test");
        User expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(userService.createUser(testCreateUserRequest)).thenReturn(expectedUser);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"id\":\"1\",\"name\":\"test\",\"surname\":\"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.surname").value("test"));
    }

    @Test
    void createUser_userAlreadyExists_returns409() throws Exception {
        CreateUserRequest expectedUser = new CreateUserRequest("test", "test");

        when(userService.createUser(expectedUser)).thenThrow(new UserAlreadyExistsException(""));

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"id\":\"1\",\"name\":\"test\",\"surname\":\"test\"}"))
                .andExpect(status().isConflict());
    }
}
