package org.mantagar.web.userevents.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mantagar.web.userevents.dto.UserNameSurnameDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exception.UserAlreadyExistsException;
import org.mantagar.web.userevents.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;

    @Test
    void shouldReturn200() throws Exception {
        List<Long> expected = List.of(1, 2, 3);
        when(userService.getAllUserIds()).thenReturn(expected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expected.size()));
    }

    @Test
    void shouldReturn200_whenUserExists() throws Exception {
        User expectedUser = new User();
        expectedUser.setId(5);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(userService.getUser(5)).thenReturn(expectedUser);

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.surname").value("test"));
    }

    @Test
    void shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.getUser(5)).thenThrow(new UserDoesNotExistException());

        mockMvc.perform(get("/users/5")).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void shouldReturn201_whenUserCreated() throws Exception {
        UserNameSurnameDTO testUserNameSurnameDTO = new UserNameSurnameDTO("test", "test");
        User expectedUser = new User();
        expectedUser.setId(5);
        expectedUser.setName("test");
        expectedUser.setSurname("test");

        when(userService.createUser(testUserNameSurnameDTO)).thenReturn(expectedUser);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"id\":\"1\",\"name\":\"test\",\"surname\":\"test\"}"))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.surname").value("test"));
    }

    @Test
    void shouldReturn409_whenUserAlreadyExists() throws Exception {
        UserNameSurnameDTO expectedUser = new UserNameSurnameDTO("test", "test");

        when(userService.createUser(expectedUser)).thenThrow(new UserAlreadyExistsException());

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"id\":\"1\",\"name\":\"test\",\"surname\":\"test\"}"))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }
}
