package org.mantagar.web.userevents.post;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.post.model.Post;
import org.mantagar.web.userevents.user.UserRepository;
import org.mantagar.web.userevents.user.exception.UserNotFoundException;
import org.mantagar.web.userevents.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Long> getAllPostIdsOfUser(Long userId) {
        verifyUserExists(userId);
        return postRepository.findAllIdsByUserId(userId);
    }

    public PostNoUserResponse getUserPost(Long userId, Long postId) {
        verifyUserExists(userId);
        return postRepository.findByIdAndUserId(postId, userId);
    }

    @Transactional
    public PostNoUserResponse createPost(CreatePostRequest createPostRequest) {
        final Optional<User> user = userRepository.findById(createPostRequest.userId());
        if (user.isEmpty()) {
            handleUserNotFoundException(createPostRequest.userId());
        }
        final Post post = new Post();
        post.setUser(user.get());
        post.setContent(createPostRequest.content());
        final Post createdPost = postRepository.save(post);
        return new PostNoUserResponse(createdPost.getId(), createdPost.getContent());
    }

    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            handleUserNotFoundException(userId);
        }
    }

    private void handleUserNotFoundException(Long userId) {
        LOG.error("User with id={} not found", userId);
        throw new UserNotFoundException("User with id=%d not found".formatted(userId));
    }
}
