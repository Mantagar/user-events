package org.mantagar.web.userevents.post;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.mantagar.web.userevents.user.UserRepository;
import org.mantagar.web.userevents.user.exception.UserDoesNotExistException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Iterable<Long> getAllUserPostIds(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public PostNoUserResponse getUserPost(Long userId, Long postId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("User with id=%d not found".formatted(userId));
        }
        return postRepository.findByUserIdAndId(userId, postId);
    }

    public PostNoUserResponse createPost(CreatePostRequest createPostRequest) {
        throw new UnsupportedOperationException("Not supported yet.");
        // TODO add test
    }
}
