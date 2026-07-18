package org.mantagar.web.userevents.controller;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.dto.PostContentDTO;
import org.mantagar.web.userevents.dto.PostNoUserDTO;
import org.mantagar.web.userevents.service.PostService;
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
    public Iterable<Long> getAllUserPostIds(@PathVariable Long userId) {
        return postService.getAllUserPostIds(userId);
    }

    @GetMapping("/{postId}")
    public PostNoUserDTO getUserPost(@PathVariable Long userId, @PathVariable Long postId) {
        return postService.getUserPost(userId, postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostNoUserDTO createUserPost(@RequestBody PostContentDTO postDTO) {
        return postService.createPost(postDTO);
    }
}
