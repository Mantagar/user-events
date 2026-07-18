package org.mantagar.web.userevents.post;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.user.exception.UserDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private PostService postService;

    @Test
    void getAllUserPostIds_userExists_returns200() throws Exception {
        List<Long> expected = List.of(1L, 2L, 3L);
        when(postService.getAllUserPostIds(5L)).thenReturn(expected);

        mockMvc.perform(get("/users/5/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expected.size()));
    }

    @Test
    void getAllUserPostIds_userNotFound_returns404() throws Exception {
        when(postService.getAllUserPostIds(5L)).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(get("/users/5/posts")).andExpect(status().isNotFound());
    }

    @Test
    void getUserPost_userExists_returns200() throws Exception {
        PostNoUserResponse expected = new PostNoUserResponse(10L, "test");
        when(postService.getUserPost(5L, 10L)).thenReturn(expected);

        mockMvc.perform(get("/users/5/posts/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.id()))
                .andExpect(jsonPath("$.content").value(expected.content()));
    }

    @Test
    void getUserPost_userNotFound_returns404() throws Exception {
        when(postService.getUserPost(5L, 10L)).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(get("/users/5/posts/10")).andExpect(status().isNotFound());
    }

    @Test
    void createUserPost_userExists_returns201() throws Exception {
        CreatePostRequest request = new CreatePostRequest(5L, "test");
        when(postService.createPost(request)).thenReturn(new PostNoUserResponse(10L, "test"));

        mockMvc.perform(
                        post("/users/5/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"userId\": 5, \"content\": \"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.content").value("test"));
    }

    @Test
    void createUserPost_userDoesNotExist_returns404() throws Exception {
        CreatePostRequest request = new CreatePostRequest(5L, "test");
        when(postService.createPost(request)).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(
                        post("/users/5/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"userId\": 5, \"content\": \"test\"}"))
                .andExpect(status().isNotFound());
    }
}
