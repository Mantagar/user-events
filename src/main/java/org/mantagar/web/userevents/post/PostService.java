package org.mantagar.web.userevents.post;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.post.model.Post;
import org.mantagar.web.userevents.user.UserRepository;
import org.mantagar.web.userevents.user.exception.UserNotFoundException;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Iterable<Long> getAllUserPostIds(Long userId) {
        verifyUserExists(userId);
        return postRepository.findAllByUserId(userId);
    }

    public PostNoUserResponse getUserPost(Long userId, Long postId) {
        verifyUserExists(userId);
        return postRepository.findByIdAndUserId(postId, userId);
    }

    // TODO add @Transactional (with default readOnly = false)
    public PostNoUserResponse createPost(CreatePostRequest createPostRequest) {
        final User user =
                userRepository
                        .findById(createPostRequest.userId())
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                "User with id=%d not found"
                                                        .formatted(createPostRequest.userId())));
        final Post post = new Post();
        post.setUser(user);
        post.setContent(createPostRequest.content());
        final Post createdPost = postRepository.save(post);
        return new PostNoUserResponse(createdPost.getId(), createdPost.getContent());
    }

    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=%d not found".formatted(userId));
        }
    }
}
