package org.mantagar.web.userevents.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.post.model.Post;
import org.mantagar.web.userevents.user.UserRepository;
import org.mantagar.web.userevents.user.exception.UserNotFoundException;
import org.mantagar.web.userevents.user.model.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock private UserRepository mockUserRepository;
    @Mock private PostRepository mockPostRepository;
    @InjectMocks private PostService postService;

    @Test
    void getAllUserPostIds_userExists_returnsIds() {
        var expected = List.of(1L, 2L, 3L);

        when(mockUserRepository.existsById(5L)).thenReturn(true);
        when(mockPostRepository.findAllByUserId(5L)).thenReturn(expected);

        assertEquals(expected, postService.getAllUserPostIds(5L));
    }

    @Test
    void getAllUserPostIds_userDoesNotExist_throwsUserNotFoundException() {
        when(mockUserRepository.existsById(5L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> postService.getAllUserPostIds(5L));
    }

    @Test
    void getUserPost_userExists_returnsPostNoUserResponse() {
        var expectedPostNoUserResponse = new PostNoUserResponse(10L, "test");

        when(mockUserRepository.existsById(5L)).thenReturn(true);
        when(mockPostRepository.findByIdAndUserId(10L, 5L)).thenReturn(expectedPostNoUserResponse);

        assertEquals(expectedPostNoUserResponse, postService.getUserPost(5L, 10L));
    }

    @Test
    void getUserPost_userDoesNotExist_throwsUserNotFoundException() {
        when(mockUserRepository.existsById(5L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> postService.getUserPost(5L, 10L));
    }

    @Test
    void createPost_userExists_returnsIds() {
        var createPostRequest = new CreatePostRequest(5L, "test");
        var post = new Post();
        post.setId(10L);
        post.setContent(createPostRequest.content());
        post.setUser(new User());
        var expectedPostNoUserResponse = new PostNoUserResponse(post.getId(), post.getContent());

        when(mockUserRepository.findById(5L)).thenReturn(Optional.of(post.getUser()));
        when(mockPostRepository.save(any(Post.class))).thenReturn(post);

        assertEquals(expectedPostNoUserResponse, postService.createPost(createPostRequest));
    }

    @Test
    void createPost_userDoesNotExist_throwsUserNotFoundException() {
        var createPostRequest = new CreatePostRequest(5L, "test");

        when(mockUserRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.createPost(createPostRequest));
    }
}
