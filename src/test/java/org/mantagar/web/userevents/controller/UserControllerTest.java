package org.mantagar.web.userevents.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mantagar.web.userevents.dto.UserDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.exceptions.UserAlreadyExistsException;
import org.mantagar.web.userevents.exceptions.UserDoesNotExistException;
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
    void testGetAllUserIds() throws Exception {
        List<Integer> mock = List.of(1, 2, 3);
        when(userService.getAllUserIds()).thenReturn(mock);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mock.size()));
    }

    @Test
    void testGetUserById_userExists() throws Exception {
        User mock = new User();
        mock.setId(5);
        mock.setName("test");
        mock.setSurname("test");

        when(userService.getUserById(5)).thenReturn(mock);

        mockMvc.perform(get("/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.surname").value("test"));
    }

    @Test
    void testGetUserById_userDoesNotExist() throws Exception {
        when(userService.getUserById(5)).thenThrow(new UserDoesNotExistException());

        mockMvc.perform(get("/users/5")).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testCreateUser_successfully() throws Exception {
        UserDTO rqMock = new UserDTO("test", "test");
        User rsMock = new User();
        rsMock.setId(5);
        rsMock.setName("test");
        rsMock.setSurname("test");

        when(userService.createUser(rqMock)).thenReturn(rsMock);

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
    void testCreateUser_userAlreadyExists() throws Exception {
        UserDTO mock = new UserDTO("test", "test");

        when(userService.createUser(mock)).thenThrow(new UserAlreadyExistsException());

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"id\":\"1\",\"name\":\"test\",\"surname\":\"test\"}"))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }
}
