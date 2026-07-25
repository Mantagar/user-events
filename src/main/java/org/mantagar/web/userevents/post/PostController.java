package org.mantagar.web.userevents.post;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public List<Long> getAllUserPostIds(@PathVariable Long userId) {
        return postService.getAllPostIdsOfUser(userId);
    }

    @GetMapping("/{postId}")
    public PostNoUserResponse getUserPost(@PathVariable Long userId, @PathVariable Long postId) {
        return postService.getUserPost(userId, postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostNoUserResponse createUserPost(@RequestBody CreatePostRequest createPostRequest) {
        return postService.createPost(createPostRequest);
    }
}
