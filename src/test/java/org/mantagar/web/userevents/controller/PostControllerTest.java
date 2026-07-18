package org.mantagar.web.userevents.controller;

import org.junit.jupiter.api.Test;
import org.mantagar.web.userevents.exception.UserDoesNotExistException;
import org.mantagar.web.userevents.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void getAllUserPostIds_whenValid_shouldReturn200() throws Exception {
        List<Long> expected = List.of(1L, 2L, 3L);
        when(postService.getAllUserPostIds(5L)).thenReturn(expected);

        mockMvc.perform(get("/users/5/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expected.size()));
    }

    @Test
    void getAllUserPostIds_whenUserNotFound_shouldReturn404() throws Exception {
        when(postService.getAllUserPostIds(5L)).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(get("/users/5/posts")).andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void getUserPost_whenValid_shouldReturn200() throws Exception{
        fail("Not implemented yet");
    }

    @Test
    void getUserPost_whenUserNotFound_shouldReturn404() throws Exception {
        fail("Not implemented yet");
    }

    @Test
    void createUserPost_whenDone_shouldReturn201() throws Exception {
        fail("Not implemented yet");
    }

}